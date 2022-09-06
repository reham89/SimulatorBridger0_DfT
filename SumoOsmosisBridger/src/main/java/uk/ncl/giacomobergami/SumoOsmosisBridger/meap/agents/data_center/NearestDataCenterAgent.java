package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.data_center;

import org.cloudbus.osmosis.core.OsmoticDatacenter;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.AbstractNetworkAgentPolicy;

public class NearestDataCenterAgent extends GeneralDataCenterAgent{
    public NearestDataCenterAgent(OsmoticDatacenter osmesisDatacenter) {
        super(osmesisDatacenter);
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }

    public NearestDataCenterAgent() {
        setPolicy(AbstractNetworkAgentPolicy.GreedyNearest);
    }
}
