package uk.ncl.giacomobergami.traffic_orchestrator;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jenetics.ext.moea.Pareto;
import org.apache.commons.lang3.tuple.Pair;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.CandidateSolutionParameters;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.LocalTimeOptimizationProblem;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.TemporalNetworkingRanking;
import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.algorithms.StringComparator;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;
import uk.ncl.giacomobergami.utils.shared_data.*;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Orchestrator {

    private final OrchestratorConfigurator conf;
    protected RSUMediator rsum;
    protected TimedVehicleMediator vehm;
    Comparator<double[]> comparator;
    Gson gson;
    File statsFolder;
    HashMap<Double, Long> problemSolvingTime;
    CandidateSolutionParameters candidate;
    HashMap<String, Vehicle> reconstructVehicles;
    List<RSU> tls;
    HashMap<String, RSU> rsuProgramHashMap;

    public Orchestrator(OrchestratorConfigurator conf) {
        this.conf = conf;
        rsum = new RSUMediator();
        vehm = new TimedVehicleMediator();
        gson = new GsonBuilder().setPrettyPrinting().create();
        statsFolder = new File(conf.output_stats_folder);
        candidate = null;
        if (conf.use_pareto_front) {
            comparator = Pareto::dominance;
        } else {
            comparator = Comparator.comparingDouble(o -> o[0] * conf.p1 + o[1] * conf.p2 + o[2] * (1 - conf.p1  - conf.p2));
        }
        problemSolvingTime = new HashMap<>();
        reconstructVehicles = new HashMap<>();
        tls = Collections.emptyList();
        rsuProgramHashMap = new HashMap<>();
    }

    protected boolean write_json(File folder, String filename, Object writable)  {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.exists() && folder.isDirectory()) {
            try {
                Files.writeString(Paths.get(new File(folder, conf.experiment_name+"_"+filename).getAbsolutePath()), gson.toJson(writable));
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    protected List<RSU> readRSU() {
        var reader = rsum.beginCSVRead(new File(conf.RSUCsvFile));
        var ls =  Lists.newArrayList(reader);
        ls.forEach(x -> {
            if (conf.reset_rsu_communication_radius > 0) {
                x.communication_radius = conf.reset_rsu_communication_radius;
            }
            if (conf.reset_max_vehicle_communication > 0) {
                x.max_vehicle_communication = conf.reset_max_vehicle_communication;
            }
        });
        try {
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ls;
    }

    protected TreeMap<Double, List<TimedVehicle>> readVehicles() {
        var reader = vehm.beginCSVRead(new File(conf.vehicleCSVFile));
        TreeMap<Double, List<TimedVehicle>> map = new TreeMap<>();
        while (reader.hasNext()) {
            var curr = reader.next();
            if (!map.containsKey(curr.simtime))
                map.put(curr.simtime, new ArrayList<>());
            map.get(curr.simtime).add(curr);
        }
        try {
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    public void run() {
        candidate = null;
        tls = readRSU();
        if (tls.isEmpty()) {
            System.err.println("WARNING: tls are empty!");
            return;
        }
        HashSet<String> vehId = new HashSet<>();
        problemSolvingTime.clear();
        SquaredCartesianDistanceFunction f = new SquaredCartesianDistanceFunction();
        List<Double> temporalOrdering = new ArrayList<>();

        reconstructVehicles.clear();
        rsuProgramHashMap.clear();
        tls.forEach(x -> rsuProgramHashMap.put(x.tl_id, x));
        HashMap<Double, ArrayList<LocalTimeOptimizationProblem.Solution>> simulationSolutions = new HashMap<>();
        var vehSet = readVehicles().entrySet();
        if (vehSet == null || vehSet.isEmpty()) {
            System.err.println("WARNING: vechicles are empty!");
            return;
        }
        for (var simTimeToVehicles : vehSet) {
            simTimeToVehicles.getValue().forEach(x -> vehId.add(x.id));
            var currTime = simTimeToVehicles.getKey();
            List<TimedVehicle> vehs2 = simTimeToVehicles.getValue();
           for (var tv : vehs2) {
               if (!reconstructVehicles.containsKey(tv.id)) {
                   reconstructVehicles.put(tv.id, new Vehicle());
               }
               reconstructVehicles.get(tv.id).dynamicInformation.put(currTime, tv);
           }
            LocalTimeOptimizationProblem solver = new LocalTimeOptimizationProblem(vehs2, tls);
            if (solver.init()) {
                if (conf.do_thresholding) {
                    if (conf.use_nearest_MEL_to_IoT) {
                        solver.setNearestMELForIoT();
                    } else {
                        solver.setAllPossibleMELForIoT();
                    }
                    if (conf.use_greedy_algorithm) {
                        solver.setGreedyPossibleTargetsForIoT(conf.use_local_demand_forecast);
                    } else if (conf.use_top_k_nearest_targets > 0) {
                        solver.setAllPossibleNearestKTargetsForCommunication(conf.use_top_k_nearest_targets, conf.use_top_k_nearest_targets_randomOne);
                    }  else {
                        solver.setAllPossibleTargetsForCommunication();
                    }
                } else {
                    solver.alwaysCommunicateWithTheNearestMel();
                }

                ArrayList<LocalTimeOptimizationProblem.Solution> sol =
                        solver.multi_objective_pareto(conf.k1, conf.k2, conf.ignore_cubic, comparator, conf.reduce_to_one, conf.update_after_flow);

                problemSolvingTime.put(currTime, solver.getRunTime());
                simulationSolutions.put(currTime, sol);
                temporalOrdering.add(currTime);
            }
        }

        List<String> tls_s = tls.stream().map(x -> x.tl_id).collect(Collectors.toList());
        List<String> veh_s = new ArrayList<>(vehId);

        System.out.println("Computing all of the possible Pareto Routing scenarios...");

        if (simulationSolutions.values().stream().anyMatch(ArrayList::isEmpty)) {
            System.err.println("NO viable solution found!");
        } else {
            Double bestResultScore = Double.MAX_VALUE;

            candidate = new CandidateSolutionParameters();
            var multiplicity = simulationSolutions.values().stream().mapToInt(ArrayList::size).reduce((a, b) -> a * b)
                    .orElse(0);
            System.out.println("Multiplicity: " + multiplicity);
            long timedBegin = System.currentTimeMillis();
            if (conf.clairvoyance) {
                TemporalNetworkingRanking.oracularBestNetworking(simulationSolutions, temporalOrdering, veh_s, bestResultScore, candidate, conf.removal, conf.addition, comparator);
            } else {
                TemporalNetworkingRanking.nonclairvoyantBestNetworking(simulationSolutions, temporalOrdering, veh_s, bestResultScore, candidate, conf.removal, conf.addition, comparator);
            }
            candidate.networkingRankingTime = (System.currentTimeMillis()- timedBegin);

            // SETTING UP THE VEHICULAR PROGRAMS
            for (var veh : vehId) {
                var vehProgram = new VehicularProgram(candidate.delta_associations.get(veh));
                for (var entry : candidate.bestResult.entrySet()) {
                    vehProgram.putDeltaRSUAssociation(entry.getKey(), entry.getValue().slowRetrievePath(veh));
                }
                vehProgram.finaliseProgram();
                reconstructVehicles.get(veh).program = vehProgram;
            }

            TreeMap<Double, Map<String, List<String>>> networkTopology = new TreeMap<>(); // Actually, for RSU programs: saving one iteration cycle
            for (var entry : candidate.bestResult.entrySet()) {
                var npMap = entry.getValue()
                        .RSUNetworkNeighbours
                        .entrySet()
                        .stream()
                        .map(x -> new ConcretePair<>(x.getKey().tl_id,
                                x.getValue().stream().map(y -> y.tl_id).collect(Collectors.toList())))
                        .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
                networkTopology.put(entry.getKey(), npMap);
                for (var vehs : entry.getValue().getAlphaAssociation()) {
                    reconstructVehicles.get(vehs.getKey().id).program.setLocalInformation(entry.getKey(), vehs.getKey());
                }
            }

            // SETTING UP THE RSU PROGRAMS
            // This concept is relevant, so if we need to remove some nodes from the simulation,
            // and to add others. This also defines with which MELs and Vehicles should an element connect/disconnect
            // for its routing
            var delta_clusters = ClusterDifference.diff(candidate.inStringTime, tls_s, StringComparator.getInstance());
            var delta_network_neighbours = ClusterDifference.diff(networkTopology, tls_s, StringComparator.getInstance());

            for (var r : tls) {
                var rsuProgram = new RSUProgram(candidate.bestResult.keySet());
                rsuProgram.finaliseProgram(delta_clusters.get(r.tl_id), delta_network_neighbours.get(r.tl_id));
                rsuProgramHashMap.get(r.tl_id).program_rsu = rsuProgram;
            }
        }
    }

    void serializeAll() {

        System.out.println("Serializing data...");
        write_json(statsFolder, "solver_time.json", problemSolvingTime);
        write_json(statsFolder, "candidate.json", candidate);
        write_json(statsFolder, conf.vehiclejsonFile, reconstructVehicles);
        write_json(statsFolder, conf.RSUJsonFile, rsuProgramHashMap);
        try {
            FileOutputStream tlsF = new FileOutputStream(Paths.get(statsFolder.getAbsolutePath(), conf.experiment_name+"_time_benchmark.csv").toFile());
            BufferedWriter flsF2 = new BufferedWriter(new OutputStreamWriter(tlsF));
            flsF2.write("sim_time,bench_time");
            flsF2.newLine();
            for (var x : problemSolvingTime.entrySet()) {
                flsF2.write(x.getKey()+","+x.getValue());
                flsF2.newLine();
            }
            flsF2.close();
            tlsF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            FileOutputStream tlsF = new FileOutputStream(Paths.get(statsFolder.getAbsolutePath(), conf.experiment_name+"_tls.csv").toFile());
            BufferedWriter flsF2 = new BufferedWriter(new OutputStreamWriter(tlsF));
            flsF2.write("Id,X,Y");
            var XMax = tls.stream().map(x -> x.x).max(Comparator.comparingDouble(y -> y)).orElseGet(() -> 0.0);
            var YMin = tls.stream().map(x -> x.y).min(Comparator.comparingDouble(y -> y)).orElseGet(() -> 0.0);
            tls.sort(Comparator.comparingDouble(sem -> {
                double x = sem.x - XMax;
                double y = sem.y - YMin;
                return (x*x)+(y*y);
            }));
            System.out.println(            tls.subList(0, Math.min(tls.size(), 8)).stream().map(x -> x.tl_id
            ).collect(Collectors.joining("\",\"","LS <- list(\"", "\")")));
            flsF2.newLine();
            for (var x : tls) {
                flsF2.write(x.tl_id +","+x.x +","+x.y);
                flsF2.newLine();
            }
            flsF2.close();
            tlsF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileOutputStream tlsF = new FileOutputStream(Paths.get(statsFolder.getAbsolutePath(), conf.experiment_name+"_tracesMatch_toplot.csv").toFile());
            BufferedWriter flsF2 = new BufferedWriter(new OutputStreamWriter(tlsF));
            flsF2.write("SimTime,Sem,NVehs");
            flsF2.newLine();
            List<TimedVehicle> e = Collections.emptyList();
            if ((candidate != null) && (candidate.inCurrentTime != null))
            for (var cp : candidate.inCurrentTime.entrySet()) {
                Double time = cp.getKey();
                for (var sem : tls) {
                    flsF2.write(time+","+sem.tl_id +","+cp.getValue().getOrDefault(sem, e).size());
                    flsF2.newLine();
                }
            }
            flsF2.close();
            tlsF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
