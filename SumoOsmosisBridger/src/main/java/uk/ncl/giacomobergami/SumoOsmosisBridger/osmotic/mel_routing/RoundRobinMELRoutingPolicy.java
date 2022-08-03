package uk.ncl.giacomobergami.SumoOsmosisBridger.osmotic.mel_routing;

import uk.ncl.giacomobergami.components.MELRoutingPolicy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobinMELRoutingPolicy implements MELRoutingPolicy {
    private Map<String, Integer> roundRobinMelMap;
    public RoundRobinMELRoutingPolicy() {
        roundRobinMelMap = new HashMap<>();
    }
    @Override
    public String apply(String abstractMel, List<String> instances) {
        if (!roundRobinMelMap.containsKey(abstractMel)){
            roundRobinMelMap.put(abstractMel,0);
        }
        int pos = roundRobinMelMap.get(abstractMel);
        String result = instances.get(pos);
        pos++;
        if (pos>= instances.size()){
            pos=0;
        }
        roundRobinMelMap.put(abstractMel,pos);
        return result;
    }
}
