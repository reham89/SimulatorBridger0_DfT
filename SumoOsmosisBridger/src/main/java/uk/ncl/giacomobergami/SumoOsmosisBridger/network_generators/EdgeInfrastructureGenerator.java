package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import uk.ncl.giacomobergami.components.loader.SubNetworkConfiguration;
import uk.ncl.giacomobergami.components.networking.*;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EdgeInfrastructureGenerator {


    public static String edgeDeviceId(int id) {
        return "edgeDevice_"+id;
    }
    public static String edgeId(int id) {
        return "edge"+id;
    }
    public static String coreId(int id) {
        return "core"+id;
    }
    public static String melId(int id) {
        return "MEL."+id;
    }


    public static Switch generateEdgeSwitch(int id, long mips) {
        return new Switch("edge", edgeId(id), mips);
    }
    public static List<Switch> generateEdgeSwitches(int n, long mips) {
        return IntStream.range(1, n+1).mapToObj(x-> generateEdgeSwitch(x, mips)).collect(Collectors.toList());
    }
    public static Switch generateCoreSwitch(int id, long mips) {
        return new Switch("edge", edgeId(id), mips);
    }
    public static List<Switch> generateCoreSwitches(int n, long mips) {
        return IntStream.range(1, n+1).mapToObj(x-> generateCoreSwitch(x, mips)).collect(Collectors.toList());
    }
    public static Host generateEdgeDevice(int id, int bw, int mips, int pes, int ram, int storage) {
        return new Host(edgeDeviceId(id),  pes, ram,  bw, storage, mips);
    }
    public static List<Host> generateEdgeDevices(int n, int bandwidth,  int mips, int pes, int ram, int storage) {
        return IntStream.range(1, n+1).mapToObj(x-> generateEdgeDevice(x, bandwidth, mips, pes, ram, storage)).collect(Collectors.toList());
    }
    public static VM generateMEL(int id, int bandwidth, String policy, double mips, int pes, int ram, int storage) {
        return new VM(melId(id), bandwidth, mips, ram, pes, policy, storage);
    }
    public static List<VM> generateVMs(int n, int bandwidth, String policy, double mips, int pes, int ram, int storage) {
        return IntStream.range(1, n+1).mapToObj(x-> generateMEL(x, bandwidth, policy, mips, pes, ram, storage)).collect(Collectors.toList());
    }

    public static class Configuration {
        public String cloud_network_name;
        public String gateway_name;
        public long gateway_mips;

        public int n_edgeDevices_and_edges;
        public int edge_device_to_edge_bw;
        public int edge_bw;
        public int between_edge_bw;

        public int n_core;
        public int n_edges_to_one_core;
        public int edge_to_core_bw;

        public int core_to_gateway_bw;

        public HostsAndVMs              hosts_and_vms;
        public DataCenterWithController network_configuration;
        StraightforwardAdjacencyList<Integer> edge_switch_network;
    }

    public static ConcretePair<SubNetworkConfiguration, List<TopologyLink>> generateLinks(EdgeInfrastructureGenerator.Configuration conf) {
        List<TopologyLink> result = new ArrayList<>();
        List<Switch> switches = new ArrayList<>();
        conf.hosts_and_vms.validate();

        switches.add(new Switch("gateway", conf.gateway_name, conf.gateway_mips));
        var hosts = generateEdgeDevices(conf.n_edgeDevices_and_edges,
                conf.hosts_and_vms.hosts_bandwidth,
                conf.hosts_and_vms.hosts_mips,
                conf.hosts_and_vms.hosts_pes,
                conf.hosts_and_vms.hosts_ram,
                conf.hosts_and_vms.hosts_storage
        );

        if ((conf.n_edgeDevices_and_edges % conf.n_edges_to_one_core)> conf.n_core) {
            conf.n_core = conf.n_edgeDevices_and_edges % conf.n_edges_to_one_core;
            System.err.println("ERROR: there should be enough cores so to map those from edge devices. Channging n_core to " + conf.n_core);
        }

        var vm = generateVMs(conf.hosts_and_vms.n_vm, conf.hosts_and_vms.vm_bw, conf.hosts_and_vms.vm_cloudletPolicy, conf.hosts_and_vms.vm_mips, conf.hosts_and_vms.vm_pes, conf.hosts_and_vms.vm_ram, conf.hosts_and_vms.vm_storage);
        for (int i = 1; i<=conf.n_edgeDevices_and_edges; i++) {
            var edgeDeviceID = edgeDeviceId(i);
            var edgeID = edgeId(i);
            switches.add(generateEdgeSwitch(i, conf.edge_bw));
            result.add(new TopologyLink(conf.cloud_network_name, edgeDeviceID, edgeID, conf.edge_device_to_edge_bw));
            var nCore = (i % conf.n_edges_to_one_core)+1;
            var nCoreID = coreId(nCore);
            if ((i / conf.n_edges_to_one_core)<= 0) {
                switches.add(generateCoreSwitch(i, conf.edge_to_core_bw));
                result.add(new TopologyLink(conf.cloud_network_name, edgeID, nCoreID, conf.edge_to_core_bw));
                result.add(new TopologyLink(conf.cloud_network_name, nCoreID, conf.gateway_name, conf.core_to_gateway_bw));
            }
            result.add(new TopologyLink(conf.cloud_network_name, edgeID,nCoreID, conf.edge_device_to_edge_bw));
        };

        conf.edge_switch_network.forEach((src, dst) -> {
            result.add(new TopologyLink(conf.cloud_network_name, edgeId(src), edgeId(dst), conf.between_edge_bw));
            result.add(new TopologyLink(conf.cloud_network_name, edgeId(dst), edgeId(src), conf.between_edge_bw));
        });

        return new ConcretePair<>(new SubNetworkConfiguration(hosts, vm, switches, conf.network_configuration),
                result
        );
    }
}