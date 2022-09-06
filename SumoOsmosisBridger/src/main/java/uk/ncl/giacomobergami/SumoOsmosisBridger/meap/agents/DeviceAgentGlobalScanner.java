package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.agents;

import org.cloudbus.agent.CentralAgent;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages.MessageWithPayload;
import uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages.PayloadFromIoTAgent;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;

import java.util.Collections;
import java.util.List;

/**
 * Sending the information to the central agent, that then is going to decide who is going to communicate with.
 */
public class DeviceAgentGlobalScanner extends DeviceAgentAbstractScanner {
    private static final List<String> central_agent_singleton = Collections.singletonList(CentralAgent.CENTRAL_AGENT_NAME);
    private static final SquaredCartesianDistanceFunction f = SquaredCartesianDistanceFunction.getInstance();
    public DeviceAgentGlobalScanner() {
        super();
    }


    @Override
    public void analyze() {
        // Asking the global agent who I should now communicate with
        super.analyze();
        if ((ls != null) && (!ls.isEmpty())) {
            // sending a message only if I have to communicate to someone
            var message = new MessageWithPayload<PayloadFromIoTAgent>();
            message.setSOURCE(getIoTDevice().getName());
            message.setDESTINATION(central_agent_singleton);
            message.setPayload(new PayloadFromIoTAgent(getIoTDevice(), ls));
            publishMessage(message);
        }

    }


}
