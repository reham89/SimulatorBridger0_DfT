package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.central_agent;

import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.AbstractNetworkAgentPolicy;

public class NearestCentralAgent extends GeneralCentralAgent {
    public NearestCentralAgent() {
        super();
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }
}
