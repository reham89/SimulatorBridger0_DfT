package uk.ncl.giacomobergami.components.sdn_routing;

import org.cloudbus.cloudsim.sdn.Link;
import org.cloudbus.cloudsim.sdn.NetworkNIC;
import org.cloudbus.cloudsim.sdn.SDNHost;
import org.cloudbus.osmosis.core.Flow;
import org.cloudbus.osmosis.core.SDNRoutingTable;

import java.util.List;

public class MaximumFlowRoutingPolicy extends SDNRoutingPolicy{

    @Override @Deprecated
    public NetworkNIC getNode(SDNHost srcHost,
                              NetworkNIC node,
                              SDNHost desthost,
                              String destApp) {
        return null;
    }

    @Override @Deprecated
    public void updateSDNNetworkGraph() {}

    @Override
    public List<NetworkNIC> buildRoute(NetworkNIC srcHost,
                                       NetworkNIC destHost,
                                       Flow pkt) {
        return null;
    }

    @Override
    public List<NetworkNIC> getRoute(int source, int dest) {
        return null;
    }

    @Override
    public List<Link> getLinks(int source, int dest) {
        return null;
    }

    @Override
    public List<SDNRoutingTable> constructRoutes(NetworkNIC node, NetworkNIC desthost, NetworkNIC srcHost) {
        return null;
    }
}
