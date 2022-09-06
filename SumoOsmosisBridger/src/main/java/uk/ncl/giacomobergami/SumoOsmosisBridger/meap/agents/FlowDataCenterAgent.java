package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

import org.cloudbus.agent.DCAgent;
import org.cloudbus.osmosis.core.OsmoticDatacenter;

public class FlowDataCenterAgent extends DCAgent {

    AbstractNetworkAgent abstractNetworkAgent;

    public FlowDataCenterAgent(OsmoticDatacenter osmesisDatacenter) {
        super(osmesisDatacenter);
        abstractNetworkAgent = new AbstractNetworkAgent();
    }

    public FlowDataCenterAgent() {
        abstractNetworkAgent = new AbstractNetworkAgent();
    }

    public AbstractNetworkAgentPolicy getPolicy() { return abstractNetworkAgent.getPolicy(); }
    public void setPolicy(AbstractNetworkAgentPolicy policy) { this.abstractNetworkAgent.setPolicy(policy); }

    @Override
    public void monitor() {
        super.monitor();
        abstractNetworkAgent.monitor();
    }

    @Override
    public void analyze() {
        super.analyze();
        abstractNetworkAgent.analyze();
    }

    @Override
    public void plan() {
        super.plan();
        abstractNetworkAgent.plan();
    }

    @Override
    public void execute() {
        super.execute();
        abstractNetworkAgent.execute();
    }
}
