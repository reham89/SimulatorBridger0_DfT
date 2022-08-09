package uk.ncl.giacomobergami.traffic_orchestrator;

import com.google.common.collect.Multimaps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import io.jenetics.ext.moea.Pareto;
import org.apache.commons.lang3.tuple.Pair;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.CandidateSolutionParameters;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.LocalTimeOptimizationProblem;
import uk.ncl.giacomobergami.traffic_orchestrator.solver.TemporalNetworkingRanking;
import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.algorithms.StringComparator;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadCSV;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadCSVMediator;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadFromVehicularProgram;
import uk.ncl.giacomobergami.utils.data.CSVMediator;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;
import uk.ncl.giacomobergami.utils.pipeline_confs.OrchestratorConfiguration;
import uk.ncl.giacomobergami.utils.pipeline_confs.TrafficConfiguration;
import uk.ncl.giacomobergami.utils.shared_data.edge.Edge;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdgeMediator;
import uk.ncl.giacomobergami.utils.shared_data.edge.EdgeProgram;
import uk.ncl.giacomobergami.utils.shared_data.iot.TimedIoT;
import uk.ncl.giacomobergami.utils.shared_data.iot.TimedIoTMediator;
import uk.ncl.giacomobergami.utils.shared_data.iot.IoT;
import uk.ncl.giacomobergami.utils.shared_data.iot.IoTProgram;
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;
import uk.ncl.giacomobergami.utils.structures.ReconstructNetworkInformation;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class CentralAgentPlanner {

    private final OrchestratorConfiguration conf;
    private final TrafficConfiguration conf2;
    protected TimedEdgeMediator rsum;
    protected TimedIoTMediator vehm;
    Comparator<double[]> comparator;
    Gson gson;
    File statsFolder;
    HashMap<Double, Long> problemSolvingTime;
    CandidateSolutionParameters candidate;
    HashMap<String, IoT> reconstructVehicles;
    ReconstructNetworkInformation timeEvolvingEdges;
    SquaredCartesianDistanceFunction f;
    List<String> tls_s;
    HashMap<Double, HashMap<String, Integer>> belongingMap;

    public CentralAgentPlanner(OrchestratorConfiguration conf, TrafficConfiguration conf2) {
        this.conf = conf;
        this.conf2 = conf2;
        rsum = new TimedEdgeMediator();
        vehm = new TimedIoTMediator();
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
        timeEvolvingEdges = null;
        f = SquaredCartesianDistanceFunction.getInstance();
        tls_s = null;
        belongingMap = new HashMap<>();
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

    protected ReconstructNetworkInformation readEdges() {
        return ReconstructNetworkInformation.fromFiles(new File(conf2.RSUCsvFile+"_timed_scc.json").getAbsoluteFile(),
                new File(conf2.RSUCsvFile+"_neighboursChange.json").getAbsoluteFile(),
                new File(conf.RSUCsvFile) );
//        Gson gson = new Gson();
//        Type sccType = new TypeToken<TreeMap<Double, List<List<String>>>>() {}.getType();
//        Type networkType = new TypeToken<HashMap<String, ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>>>>() {}.getType();
//        BufferedReader reader1 = null, reader2 = null;
//        try {
//            reader1 = new BufferedReader(new FileReader());
//            reader2 = new BufferedReader(new FileReader());
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        HashMap<String, ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>>>
//                adjacencyListVariationInTime =  gson.fromJson(reader2, networkType);
//        TreeMap<Double, List<List<String>>>
//                timed_scc = gson.fromJson(reader1, sccType);
//        try {
//            reader1.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.exit(1);
//        }
//        var reader3 = rsum.beginCSVRead();
//        HashMap<String, Edge> finalLS = new HashMap<>();
//        {
//            HashMap<String, HashMap<Double, TimedEdge>> ls = new HashMap<>();
//            while (reader3.hasNext()) {
//                var curr = reader3.next();
//                ls.computeIfAbsent(curr.id, s -> new HashMap<>()).put(curr.simtime, curr);
//            }
//            try {
//                reader3.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            for (var x : ls.entrySet()) {
//                finalLS.put(x.getKey(), new Edge(x.getValue(), null));
//            }
//        }
//        return new ReconstructNetworkInformation(adjacencyListVariationInTime,
//                                                 timed_scc,
//                                                 finalLS);
    }

    protected TreeMap<Double, List<TimedIoT>> readIoT() {
        var reader = vehm.beginCSVRead(new File(conf.vehicleCSVFile));
        TreeMap<Double, List<TimedIoT>> map = new TreeMap<>();
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
        timeEvolvingEdges = readEdges();
        HashSet<String> vehId = new HashSet<>();
        problemSolvingTime.clear();
        List<Double> temporalOrdering = new ArrayList<>();
        HashMap<Set<String>, Integer> distinct_scc_mapping = new HashMap<>();
        belongingMap.clear();
        reconstructVehicles.clear();
        HashMap<Double, ArrayList<LocalTimeOptimizationProblem.Solution>> simulationSolutions = new HashMap<>();
        var vehSet = readIoT().entrySet();
        if (vehSet.isEmpty()) {
            System.err.println("WARNING: vechicles are empty!");
            return;
        }
        for (var simTimeToVehicles : vehSet) {
            if (!timeEvolvingEdges.hasNext()) {
                throw new RuntimeException("ERROR: the TLS should have the same timing of the Vehicles");
            }
            var current = timeEvolvingEdges.next();
            {
                var tmpMap = Multimaps.asMap(current.edgeToSCC);
                HashMap<String, Integer> bmT = new HashMap<>();
                belongingMap.put(simTimeToVehicles.getKey(), bmT);
                for (var set : tmpMap.values()) {
                    var sS = new HashSet<String>();
                    for (var x : set)
                        sS.add(x.getId());
                    Integer sccId = distinct_scc_mapping.computeIfAbsent(sS, strings -> distinct_scc_mapping.size());
                    for (var x : sS)
                        bmT.put(x, sccId);
                }
            }
            simTimeToVehicles.getValue().forEach(x -> vehId.add(x.id));
            var currTime = simTimeToVehicles.getKey();
            List<TimedIoT> vehs2 = simTimeToVehicles.getValue();
           for (var tv : vehs2) {
               if (!reconstructVehicles.containsKey(tv.id)) {
                   reconstructVehicles.put(tv.id, new IoT());
               }
               reconstructVehicles.get(tv.id).dynamicInformation.put(currTime, tv);
           }
            LocalTimeOptimizationProblem solver = new LocalTimeOptimizationProblem(vehs2, current);
            if (solver.init()) {
                if (conf.do_thresholding) {
                    if (conf.use_nearest_MEL_to_IoT) {
                        solver.setNearestFirstMileMELForIoT();
                    } else {
                        solver.setAllPossibleFirstMileMELForIoT();
                    }

                    if (conf.use_greedy_algorithm) {
                        solver.setGreedyPossibleTargetsForIoT(conf.use_local_demand_forecast);
                    } else if (conf.use_top_k_nearest_targets > 0) {
                        solver.setAllPossibleNearestKTargetsForLastMileCommunication(conf.use_top_k_nearest_targets, conf.use_top_k_nearest_targets_randomOne);
                    } else {
                        solver.setAllPossibleTargetsForLastMileCommunication();
                    }
                } else {
                    solver.alwaysCommunicateWithTheNearestMel();
                }

                ArrayList<LocalTimeOptimizationProblem.Solution> sol =
                        solver.multi_objective_pareto(conf.k1, conf.k2, conf.ignore_cubic, comparator, conf.reduce_to_one, conf.update_after_flow, conf.use_scc_neighbours);

                problemSolvingTime.put(currTime, solver.getRunTime());
                simulationSolutions.put(currTime, sol);
                temporalOrdering.add(currTime);
            }
        }

        tls_s = new ArrayList<>(timeEvolvingEdges.getEdgeNodeForReconstruction().keySet());
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
                var vehProgram = new IoTProgram(candidate.delta_associations.get(veh));
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
                        .map(x -> new ImmutablePair<>(x.getKey().id,
                                x.getValue().stream().map(y -> y.id).collect(Collectors.toList())))
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
            var delta_clusters = ClusterDifference.computeTemporalDifference(candidate.inStringTime, tls_s, StringComparator.getInstance());
            var delta_network_neighbours = ClusterDifference.computeTemporalDifference(networkTopology, tls_s, StringComparator.getInstance());

            for (var cp : timeEvolvingEdges.getEdgeNodeForReconstruction().entrySet()) {
                var r = cp.getValue();
                var id = cp.getKey();
                var rsuProgram = new EdgeProgram(candidate.bestResult.keySet());
                rsuProgram.finaliseProgram(delta_clusters.get(id), delta_network_neighbours.get(id));
                r.setProgram(rsuProgram);
            }
        }
    }

    void serializeAll() {
        System.out.println("Serializing data...");
        System.out.println(" * solver_time ");
        write_json(statsFolder, "solver_time.json", problemSolvingTime);

        System.out.println(" * candidate solution ");
        write_json(statsFolder, "candidate.json", candidate);

        System.out.println(" * reconstructed vehicles ");
        write_json(statsFolder, conf.vehiclejsonFile, reconstructVehicles);

        System.out.println(" * RSU Programs ");
        write_json(statsFolder, conf.RSUJsonFile, timeEvolvingEdges.getEdgeNodeForReconstruction());

        System.out.println(" * Time for problem solving ");
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

        System.out.println(" * Just Last Mile Occupancy ");
        try {
            FileOutputStream tlsF = new FileOutputStream(Paths.get(statsFolder.getAbsolutePath(), conf.experiment_name+"_tracesMatch_toplot.csv").toFile());
            BufferedWriter flsF2 = new BufferedWriter(new OutputStreamWriter(tlsF));
            flsF2.write("SimTime,Sem,NVehs");
            flsF2.newLine();
            List<TimedIoT> e = Collections.emptyList();
            if ((candidate != null) && (candidate.inCurrentTime != null))
            for (var cp : candidate.inCurrentTime.entrySet()) {
                Double time = cp.getKey();
                for (var sem_cp : timeEvolvingEdges.getEdgeNodeForReconstruction().entrySet()) {
                    var sem_id = sem_cp.getKey();
                    var sem_ls = sem_cp.getValue();
                    flsF2.write(time+","+sem_id +","+cp.getValue().getOrDefault(
                            sem_ls.dynamicInformation.get(time), e).size());
                    flsF2.newLine();
                }
            }
            flsF2.close();
            tlsF.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(" * RSU/EDGE communication devices infrastructure");
        System.out.println("  - [WorkloadCSV]");
        var vehicularConverterToWorkflow = new WorkloadFromVehicularProgram(null);
        AtomicInteger ai = new AtomicInteger();
        CSVMediator<WorkloadCSV>.CSVWriter x = new WorkloadCSVMediator().beginCSVWrite(new File(statsFolder, "AsmathicWorkflow.csv"));
        reconstructVehicles.entrySet().stream()
                        .flatMap((Map.Entry<String, IoT> k) ->{
                            vehicularConverterToWorkflow.setNewVehicularProgram(k.getValue().getProgram());
                            return vehicularConverterToWorkflow.generateFirstMileSpecifications(conf2.step, ai, belongingMap).stream();
                        }).forEach(x::write);
        try {
            x.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
