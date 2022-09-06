package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

import org.cloudbus.osmosis.core.OsmoticDatacenter;

public class FlowDataCenterAgent extends GeneralDataCenterAgent{
    public FlowDataCenterAgent(OsmoticDatacenter osmesisDatacenter) {
        super(osmesisDatacenter);
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }

    public FlowDataCenterAgent() {
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }
}
