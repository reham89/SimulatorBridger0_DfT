package uk.ncl.giacomobergami.components.mel_routing;

import org.cloudbus.osmosis.core.OsmesisBroker;
import uk.ncl.giacomobergami.components.mel_routing.MELRoutingPolicy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoundRobinMELRoutingPolicy implements MELRoutingPolicy {
    private Map<String, Integer> roundRobinMelMap;
    public RoundRobinMELRoutingPolicy() { roundRobinMelMap = new HashMap<>(); }
    @Override
    public String apply(String abstractMel, OsmesisBroker self) {
        List<String> instances = getCandidateMELsFromPattern(abstractMel, self);
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
