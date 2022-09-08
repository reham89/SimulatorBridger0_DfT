package uk.ncl.giacomobergami.utils.shared_data.edge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.shared_data.abstracted.SimulationProgram;
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;

import java.util.*;

public class EdgeProgram implements SimulationProgram  {
    public final TreeMap<Double, ProgramDetails> neighboursAtEachSimulationTime;
    private Double startCommunicatingAtSimulationTime;
    private static Logger logger = LogManager.getRootLogger();

    public void finaliseProgram(ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>> deltaIoTDevices,
                                ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>> deltaNetworkNeighbours) {
        if (deltaIoTDevices.getRight().size() != deltaNetworkNeighbours.getRight().size()) {
            throw new RuntimeException("ERROR");
        }
        if (!Objects.equals(deltaIoTDevices.getLeft().getLeft(), deltaNetworkNeighbours.getLeft().getLeft())) {
            throw new RuntimeException("ERROR");
        }
        if (!Objects.equals(deltaIoTDevices.getLeft().getLeft(), startCommunicatingAtSimulationTime)) {
            throw new RuntimeException("ERROR");
        }
        var it1 = deltaIoTDevices.getRight().iterator();
        var it2 = deltaNetworkNeighbours.getRight().iterator();
        var it = neighboursAtEachSimulationTime.entrySet().iterator();
        var tick = it.next();
        var val = tick.getValue();
        val.isStartingProgram = true;
        val.startIoTDevice = deltaIoTDevices.getKey().getValue();
        val.startRSUNeighbours = deltaNetworkNeighbours.getKey().getValue();
        if (val.startRSUNeighbours != null) {
            logger.debug(val.startRSUNeighbours);
        }
        for (int i = 0; i<deltaIoTDevices.getValue().size(); i++) {
            tick = it.next();
            val = tick.getValue();
            val.isStartingProgram = false;
            val.startIoTDevice = null;
            val.startRSUNeighbours = null;
            val.IoTDeviceVariation = deltaIoTDevices.getValue().get(i);
            val.RSUNeighboursVariation = deltaNetworkNeighbours.getValue().get(i);
        }
    }

    public static class ProgramDetails {
        public List<String> startIoTDevice = null;
        public boolean isStartingProgram = false;
        public List<String> startRSUNeighbours = null;
        public ClusterDifference<String> IoTDeviceVariation = null;
        public ClusterDifference<String> RSUNeighboursVariation = null;

        public List<String> getStartIoTDevice() {
            return startIoTDevice;
        }
        public void setStartIoTDevice(List<String> startIoTDevice) {
            this.startIoTDevice = startIoTDevice;
        }
        public boolean isStartingProgram() {
            return isStartingProgram;
        }
        public void setStartingProgram(boolean startingProgram) {
            isStartingProgram = startingProgram;
        }
        public List<String> getStartRSUNeighbours() {
            return startRSUNeighbours;
        }
        public void setStartRSUNeighbours(List<String> startRSUNeighbours) {
            this.startRSUNeighbours = startRSUNeighbours;
        }
        public ClusterDifference<String> getIoTDeviceVariation() {
            return IoTDeviceVariation;
        }
        public void setIoTDeviceVariation(ClusterDifference<String> ioTDeviceVariation) {
            IoTDeviceVariation = ioTDeviceVariation;
        }
        public ClusterDifference<String> getRSUNeighboursVariation() {
            return RSUNeighboursVariation;
        }
        public void setRSUNeighboursVariation(ClusterDifference<String> RSUNeighboursVariation) {
            this.RSUNeighboursVariation = RSUNeighboursVariation;
        }
    }

    public EdgeProgram(Set<Double> doubles) {
        startCommunicatingAtSimulationTime = Double.MAX_VALUE;
        neighboursAtEachSimulationTime = new TreeMap<>();
        for (Double delta : doubles) {
            neighboursAtEachSimulationTime.put(delta, new ProgramDetails());
        }
        startCommunicatingAtSimulationTime = neighboursAtEachSimulationTime.firstKey();
    }

}
