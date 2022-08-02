package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.List;

public class BogusNetworkGenerator implements NetworkGenerator {

    private BogusNetworkGenerator() {}
    private static BogusNetworkGenerator self = null;
    public static BogusNetworkGenerator getInstance() {
        if (self == null)
            self = new BogusNetworkGenerator();
        return self;
    }

    @Override
    public StraightforwardAdjacencyList<RSU> apply(List<RSU> rsus) {
        return null;
    }
}
