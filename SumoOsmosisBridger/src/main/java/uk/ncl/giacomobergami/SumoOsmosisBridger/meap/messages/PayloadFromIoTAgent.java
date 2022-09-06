package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages;

import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDevice;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;

import java.util.List;

public class PayloadFromIoTAgent {
    public IoTDevice sender;
    public List<ImmutablePair<EdgeDataCenter, EdgeDevice>> candidates;

    public PayloadFromIoTAgent(IoTDevice sender, List<ImmutablePair<EdgeDataCenter, EdgeDevice>> candidates) {
        this.sender = sender;
        this.candidates = candidates;
    }

    public IoTDevice getSender() {
        return sender;
    }

    public void setSender(IoTDevice sender) {
        this.sender = sender;
    }

    public List<ImmutablePair<EdgeDataCenter, EdgeDevice>> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<ImmutablePair<EdgeDataCenter, EdgeDevice>> candidates) {
        this.candidates = candidates;
    }
}
