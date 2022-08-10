package uk.ncl.giacomobergami.components.mel_routing;

import org.cloudbus.osmosis.core.OsmoticBroker;
import uk.ncl.giacomobergami.components.iot.IoTDevice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobinMELRoutingPolicy implements MELRoutingPolicy {
    private Map<String, Integer> roundRobinMelMap;
    public RoundRobinMELRoutingPolicy() { roundRobinMelMap = new HashMap<>(); }
    @Override
    public String apply(IoTDevice IoTDevice, String abstractMel, OsmoticBroker self) {
        List<String> instances = getCandidateMELsFromPattern(abstractMel, self);
        if (!roundRobinMelMap.containsKey(abstractMel)){
            roundRobinMelMap.put(abstractMel,0);
        }
        int pos = roundRobinMelMap.get(abstractMel);
        if (pos>= instances.size()){
            pos=0;
        }
        String result = instances.get(pos);
        pos++;
        if (pos>= instances.size()){
            pos=0;
        }
        roundRobinMelMap.put(abstractMel,pos);
        return result;
    }
}
