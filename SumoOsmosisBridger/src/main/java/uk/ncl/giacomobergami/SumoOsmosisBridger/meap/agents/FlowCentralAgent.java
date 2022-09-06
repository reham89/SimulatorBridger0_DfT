package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

import org.cloudbus.agent.CentralAgent;

public class FlowCentralAgent extends GeneralCentralAgent {
    public FlowCentralAgent() {
        super();
        setPolicy(AbstractNetworkAgentPolicy.OptimalMinCostFlux);
    }
}
