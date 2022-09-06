package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

public class NearestCentralAgent extends GeneralCentralAgent {
    public NearestCentralAgent() {
        super();
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }
}
