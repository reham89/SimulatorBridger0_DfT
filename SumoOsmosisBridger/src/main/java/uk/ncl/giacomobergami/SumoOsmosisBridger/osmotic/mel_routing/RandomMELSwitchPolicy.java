package uk.ncl.giacomobergami.SumoOsmosisBridger.osmotic.mel_routing;

import org.apache.commons.math3.random.MersenneTwister;
import org.cloudbus.osmosis.core.OsmoticBroker;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.components.mel_routing.MELSwitchPolicy;

import java.util.List;

public class RandomMELSwitchPolicy implements MELSwitchPolicy {
    private MersenneTwister mt;
    public RandomMELSwitchPolicy() { mt = new MersenneTwister(); }
    @Override
    public String apply(IoTDevice ignored, String s, OsmoticBroker self) {
        List<String> instances = getCandidateMELsFromPattern(s, self);
        return instances.get(mt.nextInt(instances.size()));
    }
}
