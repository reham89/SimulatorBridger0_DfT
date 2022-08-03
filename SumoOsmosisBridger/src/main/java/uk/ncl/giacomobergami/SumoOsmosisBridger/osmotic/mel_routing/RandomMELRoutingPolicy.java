package uk.ncl.giacomobergami.SumoOsmosisBridger.osmotic.mel_routing;

import org.apache.commons.math3.random.MersenneTwister;
import uk.ncl.giacomobergami.components.mel_routing.MELRoutingPolicy;

import java.util.List;

public class RandomMELRoutingPolicy implements MELRoutingPolicy {
    private MersenneTwister mt;
    public RandomMELRoutingPolicy() {
        mt = new MersenneTwister();
    }
    @Override
    public String apply(String s, List<String> strings) {
        return strings.get(mt.nextInt(strings.size()));
    }
}
