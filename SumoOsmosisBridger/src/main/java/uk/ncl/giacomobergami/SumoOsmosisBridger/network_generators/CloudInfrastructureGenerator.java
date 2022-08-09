package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.collect.HashMultimap;
import uk.ncl.giacomobergami.components.loader.SubNetworkConfiguration;
import uk.ncl.giacomobergami.components.networking.*;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CloudInfrastructureGenerator {

    public static String coreId(int id) {
        return "core"+id;
    }
    public static String aggregateId(int id) {
        return "aggregate"+id;
    }
    public static String edgeId(int id) {
        return "edge"+id;
    }
    public static String vmId(int id) {
        return "VM_"+id;
    }
    public static String hostId(int id) {
        return "host"+id;
    }

    public static int numberOfGroups(int targetRow, int groupSize) {
        if (targetRow < groupSize) return  1;
        else if ((0 < groupSize) && (groupSize <= targetRow)) return 1 + (targetRow-groupSize);
        else return targetRow;
    }

    public static Switch generateCoreSwitch(int id, long mips) {
        return new Switch("core", coreId(id), mips);
    }
    public static List<Switch> generateCoreSwitches(int n, long mips) {
        return IntStream.range(1, n+1).mapToObj(x-> generateCoreSwitch(x, mips)).collect(Collectors.toList());
    }
    public static Switch generateAggregateSwitch(int id, long mips) {
        return new Switch("aggregate", aggregateId(id), mips);
    }
    public static List<Switch> generateAggregateSwitches(int n, long mips) {
        return IntStream.range(1, n+1).mapToObj(x-> generateAggregateSwitch(x, mips)).collect(Collectors.toList());
    }
    public static Switch generateEdgeSwitch(int id, long mips) {
        return new Switch("edge", edgeId(id), mips);
    }
    public static List<Switch> generateEdgeSwitches(int n, long mips) {
        return IntStream.range(1, n+1).mapToObj(x-> generateEdgeSwitch(x, mips)).collect(Collectors.toList());
    }
    public static VM generateVirtualMachine(int id, int bandwidth, String policy, double mips, int pes, int ram, int storage) {
        return new VM(vmId(id), bandwidth, mips, ram, pes, policy, storage);
    }
    public static List<VM> generateVMs(int n, int bandwidth, String policy, double mips, int pes, int ram, int storage) {
        return IntStream.range(1, n+1).mapToObj(x-> generateVirtualMachine(x, bandwidth, policy, mips, pes, ram, storage)).collect(Collectors.toList());
    }
    public static Host generateHost(int id, int bw, int mips, int pes, int ram, int storage) {
        return new Host(hostId(id),  pes, ram,  bw, storage, mips, 0, 0, 0, 0);
    }
    public static List<Host> generateHosts(int n, int bandwidth,  int mips, int pes, int ram, int storage) {
        return IntStream.range(1, n+1).mapToObj(x-> generateHost(x, bandwidth, mips, pes, ram, storage)).collect(Collectors.toList());
    }

    public static class Configuration {
        public String cloud_network_name;
        public String gateway_name;
        public long gateway_mips;

        public int n_cores;
        public long cores_mips;
        public int gateway_to_core_bandwidth;

        public int n_aggregates;
        public long aggregates_mpis;
        public int core_to_aggregate_bandwidth;

        public int n_edges;
        public int edges_mips;
        public int n_edges_group_size;
        public int aggregate_to_edge_bandwidth;

        public HostsAndVMs              hosts_and_vms;
        public DataCenterWithController network_configuration;

        public HostsAndVMs getHosts_and_vms() {
            return hosts_and_vms;
        }

        public void setHosts_and_vms(HostsAndVMs hosts_and_vms) {
            this.hosts_and_vms = hosts_and_vms;
        }

        public String getCloud_network_name() {
            return cloud_network_name;
        }

        public void setCloud_network_name(String cloud_network_name) {
            this.cloud_network_name = cloud_network_name;
        }

        public String getGateway_name() {
            return gateway_name;
        }

        public void setGateway_name(String gateway_name) {
            this.gateway_name = gateway_name;
        }

        public long getGateway_mips() {
            return gateway_mips;
        }

        public void setGateway_mips(long gateway_mips) {
            this.gateway_mips = gateway_mips;
        }

        public int getN_cores() {
            return n_cores;
        }

        public void setN_cores(int n_cores) {
            this.n_cores = n_cores;
        }

        public long getCores_mips() {
            return cores_mips;
        }

        public void setCores_mips(long cores_mips) {
            this.cores_mips = cores_mips;
        }

        public int getGateway_to_core_bandwidth() {
            return gateway_to_core_bandwidth;
        }

        public void setGateway_to_core_bandwidth(int gateway_to_core_bandwidth) {
            this.gateway_to_core_bandwidth = gateway_to_core_bandwidth;
        }

        public int getN_aggregates() {
            return n_aggregates;
        }

        public void setN_aggregates(int n_aggregates) {
            this.n_aggregates = n_aggregates;
        }

        public long getAggregates_mpis() {
            return aggregates_mpis;
        }

        public void setAggregates_mpis(long aggregates_mpis) {
            this.aggregates_mpis = aggregates_mpis;
        }

        public int getCore_to_aggregate_bandwidth() {
            return core_to_aggregate_bandwidth;
        }

        public void setCore_to_aggregate_bandwidth(int core_to_aggregate_bandwidth) {
            this.core_to_aggregate_bandwidth = core_to_aggregate_bandwidth;
        }

        public int getN_edges() {
            return n_edges;
        }

        public void setN_edges(int n_edges) {
            this.n_edges = n_edges;
        }

        public int getEdges_mips() {
            return edges_mips;
        }

        public void setEdges_mips(int edges_mips) {
            this.edges_mips = edges_mips;
        }

        public int getN_edges_group_size() {
            return n_edges_group_size;
        }

        public void setN_edges_group_size(int n_edges_group_size) {
            this.n_edges_group_size = n_edges_group_size;
        }

        public int getAggregate_to_edge_bandwidth() {
            return aggregate_to_edge_bandwidth;
        }

        public void setAggregate_to_edge_bandwidth(int aggregate_to_edge_bandwidth) {
            this.aggregate_to_edge_bandwidth = aggregate_to_edge_bandwidth;
        }

        public DataCenterWithController getNetwork_configuration() {
            return network_configuration;
        }

        public void setNetwork_configuration(DataCenterWithController network_configuration) {
            this.network_configuration = network_configuration;
        }
    }

    public static SubNetworkConfiguration generate(Configuration conf, List<TopologyLink> result) {
//        List<TopologyLink> result = new ArrayList<>();
        List<Switch> switches = new ArrayList<>();
        conf.hosts_and_vms.validate();
        switches.add(new Switch("gateway", conf.gateway_name, conf.gateway_mips));

        // Gateway to cores
        var cores = generateCoreSwitches(conf.n_cores, conf.cores_mips);
        for (var core : cores) result.add(new TopologyLink(conf.cloud_network_name, conf.gateway_name, core.name, conf.gateway_to_core_bandwidth));
        switches.addAll(cores);
        cores.clear();

        // Cores to aggregates
        var aggregates = generateAggregateSwitches(conf.n_aggregates, conf.aggregates_mpis);
        int half_cores = conf.n_cores/2;
        int half_aggregates = conf.n_aggregates/2;
        for (int i = 1; i<=half_cores; i++) {
            for (int j = 0; (j<half_aggregates); j++) {
                int jId = j*2+1;
                if (jId <= conf.n_aggregates) {
                    result.add(new TopologyLink(conf.cloud_network_name, coreId(i), aggregateId(jId), conf.core_to_aggregate_bandwidth));
                }
            }
        }
        for (int i = half_cores; i<=conf.n_cores; i++) {
            for (int j = 1; (j<half_aggregates); j++) {
                int jId = j*2;
                if (jId <= conf.n_aggregates) {
                    result.add(new TopologyLink(conf.cloud_network_name, coreId(i), aggregateId(jId), conf.core_to_aggregate_bandwidth));
                }
            }
        }
        switches.addAll(aggregates);
        aggregates.clear();

        // Aggregates to edges
        var edges = generateEdgeSwitches(conf.n_edges, conf.edges_mips);
        var nGroups = numberOfGroups(conf.n_edges, conf.n_edges_group_size);
        int splits;
        if (nGroups >= conf.n_aggregates) {
            splits = numberOfGroups(nGroups, conf.n_aggregates);
            for (int prev_i = 1; prev_i<=conf.n_aggregates; prev_i++) {
                int finalAssignJ = prev_i+splits-1+conf.n_edges_group_size-1;
                for (int assign_j = prev_i; assign_j<=finalAssignJ; assign_j++)
                    result.add(new TopologyLink(conf.cloud_network_name, aggregateId(prev_i), edgeId(assign_j), conf.aggregate_to_edge_bandwidth));
            }
        } else {
            splits = numberOfGroups(conf.n_aggregates, nGroups);
            HashMultimap<Integer, Integer> m = HashMultimap.create();
            for (int i = 1; i<=nGroups; i++) {
                for (int prev_i = i; prev_i<i+splits; prev_i++) {
                    for (int next_j = i; next_j<i+nGroups-1; next_j++) {
                        m.put(prev_i, next_j);
                    }
                }
            }
            for (var x : m.asMap().entrySet()) {
                var prev_i = x.getKey();
                for (var assign_j : x.getValue()) {
                    result.add(new TopologyLink(conf.cloud_network_name, aggregateId(prev_i), edgeId(assign_j), conf.aggregate_to_edge_bandwidth));
                }
            }
        }
        switches.addAll(edges);
        edges.clear();

        var hosts = generateHosts(
                conf.hosts_and_vms.n_hosts_per_edges * conf.n_edges,
                conf.hosts_and_vms.hosts_bandwidth,
                conf.hosts_and_vms.hosts_mips,
                conf.hosts_and_vms.hosts_pes,
                conf.hosts_and_vms.hosts_ram,
                conf.hosts_and_vms.hosts_storage
        );
        for (int i = 1; i<conf.n_edges; i++) {
            for (int j = 1; j<=conf.hosts_and_vms.n_hosts_per_edges; j++) {
                result.add(new TopologyLink(conf.cloud_network_name, edgeId(i), hostId(j*(i-1)+1), conf.aggregate_to_edge_bandwidth));
            }
        }

        var vm = generateVMs(conf.hosts_and_vms.n_vm, conf.hosts_and_vms.vm_bw, conf.hosts_and_vms.vm_cloudletPolicy, conf.hosts_and_vms.vm_mips, conf.hosts_and_vms.vm_pes, conf.hosts_and_vms.vm_ram, conf.hosts_and_vms.vm_storage);
        return new SubNetworkConfiguration(hosts, vm, switches, conf.network_configuration);

    }
}
