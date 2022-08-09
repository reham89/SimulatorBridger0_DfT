package uk.ncl.giacomobergami.SumoOsmosisBridger.osmotic.mel_routing;

import com.eatthepath.jvptree.DistanceFunction;
import org.cloudbus.osmosis.core.OsmoticBroker;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.components.mel_routing.RoundRobinMELRoutingPolicy;
import uk.ncl.giacomobergami.utils.gir.CartesianPoint;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;

import java.util.ArrayList;
import java.util.List;

public class MELNearestDistanceRouting extends RoundRobinMELRoutingPolicy {
    private final DistanceFunction<CartesianPoint> f = SquaredCartesianDistanceFunction.getInstance();

    @Override
    public List<String> getCandidateMELsFromPattern(String pattern, OsmoticBroker self) {
        if (pattern.equals("*")) {
            return new ArrayList<>(self.iotVmIdByName.keySet());
        } else {
            return super.getCandidateMELsFromPattern(pattern, self);
        }
    }

    @Override
    public String apply(IoTDevice ioTDevice, String melName, OsmoticBroker self) {
        double iotSqRange = ioTDevice.mobility.signalRange * ioTDevice.mobility.signalRange;
        double minimumDistance = Double.MAX_VALUE;
        String minimumCandidate = null;
        for (String candidate : getCandidateMELsFromPattern(melName, self)) {
            var hosts = self.selectVMFromHostPredicate(melName);
            for (String host : hosts) {
                var edgeHost = self.resolveEdgeDeviceFromId(host);
                double edgeSqRange = edgeHost.signalRange * edgeHost.signalRange;
                var squaredDistance = f.getDistance(ioTDevice, edgeHost);
                if (squaredDistance <= Math.min(iotSqRange, edgeSqRange)) {
                    if (squaredDistance <= minimumDistance) {
                        minimumDistance = squaredDistance;
                        minimumCandidate = host;
                    }
                }
            }
        }
        return minimumCandidate;
    }
}
