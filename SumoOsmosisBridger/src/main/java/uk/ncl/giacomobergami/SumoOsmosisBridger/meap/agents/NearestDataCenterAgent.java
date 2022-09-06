package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

import org.cloudbus.osmosis.core.OsmoticDatacenter;

public class NearestDataCenterAgent extends GeneralDataCenterAgent{
    public NearestDataCenterAgent(OsmoticDatacenter osmesisDatacenter) {
        super(osmesisDatacenter);
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }

    public NearestDataCenterAgent() {
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }
}
