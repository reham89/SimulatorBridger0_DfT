package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.data_center;

import org.cloudbus.osmosis.core.OsmoticDatacenter;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.AbstractNetworkAgentPolicy;

public class FlowDataCenterAgent extends GeneralDataCenterAgent{
    public FlowDataCenterAgent(OsmoticDatacenter osmesisDatacenter) {
        super(osmesisDatacenter);
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }

    public FlowDataCenterAgent() {
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }
}
