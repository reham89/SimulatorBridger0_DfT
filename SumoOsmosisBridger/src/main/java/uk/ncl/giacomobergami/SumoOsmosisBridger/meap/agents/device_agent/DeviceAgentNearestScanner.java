package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents.device_agent;

import org.cloudbus.agent.CentralAgent;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages.MessageWithPayload;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages.PayloadFromIoTAgent;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Communicating only to the node pertaining to the nearset network and, in there,
 * We're choosing which is the best node.
 */
public class DeviceAgentNearestScanner extends DeviceAgentAbstractScanner {
    private static final List<String> central_agent_singleton = Collections.singletonList(CentralAgent.CENTRAL_AGENT_NAME);
    private static final SquaredCartesianDistanceFunction f = SquaredCartesianDistanceFunction.getInstance();

    public DeviceAgentNearestScanner() {
        super();
    }


    @Override
    public void analyze() {
        // Asking the global agent who I should now communicate with
        super.analyze();
        var self = getIoTDevice();
        ls.stream()
                .min(Comparator.comparingDouble(o -> f.getDistance(self, o.getRight())))
                .ifPresent(singleton -> {
                    var message = new MessageWithPayload<PayloadFromIoTAgent>();
                    message.setSOURCE(getIoTDevice().getName());
                    message.setDESTINATION(central_agent_singleton);
                    message.setPayload(new PayloadFromIoTAgent(getIoTDevice(), Collections.singletonList(singleton)));
                    publishMessage(message);
                });
    }


}
