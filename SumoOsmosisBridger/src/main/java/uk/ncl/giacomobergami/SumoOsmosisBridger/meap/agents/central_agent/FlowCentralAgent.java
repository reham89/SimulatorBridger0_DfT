package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.central_agent;

import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.AbstractNetworkAgentPolicy;

public class FlowCentralAgent extends GeneralCentralAgent {
    public FlowCentralAgent() {
        super();
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }
}
