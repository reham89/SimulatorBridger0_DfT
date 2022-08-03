package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.netgen;

import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.Collection;
public class BogusNetworkGenerator implements NetworkGenerator {
    private BogusNetworkGenerator() {}
    private static BogusNetworkGenerator self = null;
    public static BogusNetworkGenerator getInstance() {
        if (self == null)
            self = new BogusNetworkGenerator();
        return self;
    }

    @Override
    public StraightforwardAdjacencyList<RSU> apply(Collection<RSU> rsus) {
        return null;
    }
}
