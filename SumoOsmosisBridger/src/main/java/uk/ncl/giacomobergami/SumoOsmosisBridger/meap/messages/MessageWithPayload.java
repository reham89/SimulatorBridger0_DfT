package uk.ncl.giacomobergami.SumoOsmosisBridger.meap.messages;

import org.cloudbus.agent.AgentMessage;

public class MessageWithPayload<Payload> extends AgentMessage {
    private Payload payload;
    public Payload getPayload() {
        return payload;
    }
    public void setPayload(Payload payload) {
        this.payload = payload;
    }
}
