package uk.ncl.giacomobergami.utils.shared_data;

import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadCSV;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;
import uk.ncl.giacomobergami.utils.structures.Union2;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class VehicularProgram {

    public void setLocalInformation(Double key, TimedVehicle key1) {
        pathingAtEachSimulationTime.get(key).localInformation = key1;
    }

    public class ProgramDetails {
        public final List<Union2<TimedVehicle, RSU>> shortest_path;
        public boolean isStartingProgram;
        public List<String> setInitialClusterConnection;
        public ClusterDifference<String> setConnectionVariation;
        public TimedVehicle localInformation;

        public List<Union2<TimedVehicle, RSU>> getShortest_path() {
            return shortest_path;
        }

        public boolean isStartingProgram() {
            return isStartingProgram;
        }

        public void setStartingProgram(boolean startingProgram) {
            isStartingProgram = startingProgram;
        }

        public List<String> getSetInitialClusterConnection() {
            return setInitialClusterConnection;
        }

        public void setSetInitialClusterConnection(List<String> setInitialClusterConnection) {
            this.setInitialClusterConnection = setInitialClusterConnection;
        }

        public ClusterDifference<String> getSetConnectionVariation() {
            return setConnectionVariation;
        }

        public void setSetConnectionVariation(ClusterDifference<String> setConnectionVariation) {
            this.setConnectionVariation = setConnectionVariation;
        }

        public TimedVehicle getLocalInformation() {
            return localInformation;
        }

        public void setLocalInformation(TimedVehicle localInformation) {
            this.localInformation = localInformation;
        }

        public ProgramDetails(List<Union2<TimedVehicle, RSU>> shortest_path) {
            this.shortest_path = shortest_path;
            isStartingProgram = false;
            setInitialClusterConnection = null;
            setConnectionVariation = null;
            localInformation = null;
        }
    }

    public final TreeMap<Double, ProgramDetails> pathingAtEachSimulationTime;
    public ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>> clusterConnection;
    public double startCommunicatingAtSimulationTime = Double.MAX_VALUE;

    public TreeMap<Double, ProgramDetails> getPathingAtEachSimulationTime() {
        return pathingAtEachSimulationTime;
    }

    public ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>> getClusterConnection() {
        return clusterConnection;
    }

    public void setClusterConnection(ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>> clusterConnection) {
        this.clusterConnection = clusterConnection;
    }

    public double getStartCommunicatingAtSimulationTime() {
        return startCommunicatingAtSimulationTime;
    }

    public void setStartCommunicatingAtSimulationTime(double startCommunicatingAtSimulationTime) {
        this.startCommunicatingAtSimulationTime = startCommunicatingAtSimulationTime;
    }

    public VehicularProgram(ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>> clusterConnection) {
        this.clusterConnection = clusterConnection;
        this.pathingAtEachSimulationTime = new TreeMap<>();
    }

    public void putDeltaRSUAssociation(Double key, List<Union2<TimedVehicle, RSU>> retrievePath) {
        pathingAtEachSimulationTime.put(key, new ProgramDetails(retrievePath));
        if (key < startCommunicatingAtSimulationTime) {
            startCommunicatingAtSimulationTime = key;
        }
    }





    public void finaliseProgram() {
        if (clusterConnection.getRight().isEmpty() != pathingAtEachSimulationTime.isEmpty())
            throw new RuntimeException("ERROR");
        else if (clusterConnection.getRight().size()+1 != pathingAtEachSimulationTime.size()) {
            throw new RuntimeException("ERROR");
        }
        var it = pathingAtEachSimulationTime.entrySet().iterator();
        for (int i = 0; i<clusterConnection.getRight().size(); i++) {
            var tick = it.next();
            if (i == 0) {
                tick.getValue().isStartingProgram = true;
                if (!Objects.equals(tick.getKey(), clusterConnection.getKey().getLeft())) {
                    throw new RuntimeException("ERROR!");
                }
                tick.getValue().setInitialClusterConnection = clusterConnection.getLeft().getValue();
            } else {
                tick.getValue().setConnectionVariation = clusterConnection.getValue().get(i-1);
            }
        }
        clusterConnection = null; // Freeing some memory
    }
}
