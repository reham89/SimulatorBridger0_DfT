package uk.ncl.giacomobergami.components.mel_routing;

import org.cloudbus.osmosis.core.OsmoticBroker;
import uk.ncl.giacomobergami.components.iot.IoTDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public interface MELRoutingPolicy extends Predicate<String> {
    default boolean test(String s) { return s.matches("^\\S*.[*]$"); }
    default List<String> getCandidateMELsFromPattern(String pattern,
                                                     OsmoticBroker self) {
        List<String> instances = new ArrayList<>();
        String reg = pattern.replaceAll("(.\\*)$", "");
        reg = "^"+reg+".[0-9]+$";
        for(String melName: self.iotVmIdByName.keySet()){
            if (melName.matches(reg)){
                instances.add(melName);
            }
        }
        return instances;
    }

    String apply(IoTDevice ioTDevice, String melName, OsmoticBroker broker);
}
