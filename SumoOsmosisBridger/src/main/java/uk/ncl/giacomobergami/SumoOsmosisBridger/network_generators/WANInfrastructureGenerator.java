package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import uk.ncl.giacomobergami.components.networking.Switch;
import uk.ncl.giacomobergami.components.networking.TopologyLink;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class WANInfrastructureGenerator {

    public static class Configuration {
        public int mips;
        public int bandwidth;
    }

    public static String SDWANRouter(int i){ return "sdwan_router_"+i; }
    public static Switch generateSDWANRouterSwitch(int id, long mips) {
        return new Switch("gateway", SDWANRouter(id), mips);
    }

    public static List<Switch> generate(List<CloudInfrastructureGenerator.Configuration> cloudNets,
                                                                           List<EdgeInfrastructureGenerator.Configuration>  edgeNets,
                                                                                  Configuration conf,
                                                                List<TopologyLink> links) {
        AtomicInteger ai = new AtomicInteger(1);
        List<Switch> switches = new ArrayList<>(cloudNets.size());
//        List<TopologyLink> links = new ArrayList<>();
        for (int i = 1; i<=edgeNets.size(); i++) {
            links.add(new TopologyLink("sdwan", edgeNets.get(i).gateway_name, SDWANRouter(i), conf.bandwidth));
            switches.add(generateSDWANRouterSwitch(ai.getAndIncrement(), conf.mips));
        }
        for (int i = 1; i<=cloudNets.size(); i++) {
            links.add(new TopologyLink("sdwan", cloudNets.get(i).gateway_name, SDWANRouter(i), conf.bandwidth));
            switches.add(generateSDWANRouterSwitch(ai.getAndIncrement(), conf.mips));
        }
        for (int j = 1; j<=cloudNets.size()*edgeNets.size(); j++) {
            var id = ai.getAndIncrement();
            switches.add(generateSDWANRouterSwitch(id, conf.mips));

            for (int i = 1; i<=edgeNets.size(); i++) {
                links.add(new TopologyLink("sdwan", switches.get(i).name, SDWANRouter(id), conf.bandwidth));
            }
        }
        for (int j = 1; j<=cloudNets.size()*edgeNets.size(); j++) {
            var id = ai.getAndIncrement();
            switches.add(generateSDWANRouterSwitch(id, conf.mips));

            for (int i = edgeNets.size()+1; i<=cloudNets.size(); i++) {
                links.add(new TopologyLink("sdwan", SDWANRouter(id), switches.get(i).name, conf.bandwidth));
            }
        }
        return switches;
    }
}
