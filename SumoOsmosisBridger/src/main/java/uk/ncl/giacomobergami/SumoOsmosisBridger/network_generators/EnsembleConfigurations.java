package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators.from_traffic_data.EdgeNetworksGenerator;
import uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators.from_traffic_data.IoTEntityGenerator;
import uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators.from_traffic_data.TimeTicker;
import uk.ncl.giacomobergami.components.iot.IoTDeviceTabularConfiguration;
import uk.ncl.giacomobergami.components.loader.GlobalConfigurationSettings;
import uk.ncl.giacomobergami.components.loader.SubNetworkConfiguration;
import uk.ncl.giacomobergami.components.networking.TopologyLink;
import uk.ncl.giacomobergami.utils.annotations.Input;
import uk.ncl.giacomobergami.utils.annotations.Output;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadCSV;
import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class EnsembleConfigurations {

    private final IoTEntityGenerator ioTEntityGenerator;
    private final EdgeNetworksGenerator edgeNetworkGenerator;
    private final CloudInfrastructureGenerator.Configuration cloud;
    private final  EdgeInfrastructureGenerator.Configuration edge;
    private final WANInfrastructureGenerator.Configuration wan_conf;

    public EnsembleConfigurations(IoTEntityGenerator ioTEntityGenerator,
                                  EdgeNetworksGenerator edgeNetworkGenerator,
                                  CloudInfrastructureGenerator.Configuration cloud,
                                  EdgeInfrastructureGenerator.Configuration edge,
                                  WANInfrastructureGenerator.Configuration wan_conf) {
        this.ioTEntityGenerator = ioTEntityGenerator;
        this.edgeNetworkGenerator = edgeNetworkGenerator;

        this.cloud = cloud;
        this.edge = edge;
        this.wan_conf = wan_conf;
    }

    /**
     * Generate the cloud configuration given the parametric settings
     *
     * @param numberOfClouds            Number of total cloud processing infrastructures
     * @param IoTMultiplicityForVMs     Number of VM per IoT device
     * @param nSCC                      The forecasted number of edge elements
     * @param globalCloud               Global configuration shared among all of the subnetworks
     * @return     Cloud configurations
     */
    private List<CloudInfrastructureGenerator.Configuration> setCloudPolicyFromIoTNumbers(int numberOfClouds,
                                                                                         int IoTMultiplicityForVMs,
                                                                                         int nSCC,
                                                                                         CloudInfrastructureGenerator.Configuration globalCloud) {
        List<CloudInfrastructureGenerator.Configuration> ls = new ArrayList<>();
        int IoTNumber = ioTEntityGenerator.maximumNumberOfCommunicatingVehicles();

        // Assuming to set VM in the number of IoTNumber * IoTMultiplicityForVMs / numberOfClouds
        globalCloud.hosts_and_vms.n_vm = IoTNumber * IoTMultiplicityForVMs / numberOfClouds;

        // Assuming that the pes gives an idea of the instantiation, therefore from this we scale dound the hosts
        int HostsNumber = (int)Math.ceil(((double)globalCloud.hosts_and_vms.n_vm) / (((double)globalCloud.hosts_and_vms.hosts_pes) / ((double)globalCloud.hosts_and_vms.vm_pes)));
        globalCloud.n_edges = (int)Math.ceil(((double)HostsNumber) / ((double) globalCloud.hosts_and_vms.n_hosts_per_edges));
        globalCloud.n_cores = nSCC;
        globalCloud.n_aggregates = Math.max((globalCloud.n_edges +globalCloud.n_cores)/IoTMultiplicityForVMs, globalCloud.n_edges*2);

        for (int i = 1; i<=numberOfClouds; i++) {
            // Setting the remaining instance-dependant parameters
            // Cloud name
            globalCloud.network_configuration.datacenter_name = "Cloud_"+i;
            globalCloud.cloud_network_name = "Cloud_"+i;
            // controller name
            globalCloud.network_configuration.controller_name = "dc"+i+"_sdn"+i;
            // gateway name
            globalCloud.gateway_name = "dc"+i+"_gateway";

            // When finished, copy the current configuration
            var local = globalCloud.copy();
            ls.add(local);
        }

        return ls;
    }

    public List<EdgeInfrastructureGenerator.Configuration> generateConfigurationForSimulationTime(double tick, EdgeInfrastructureGenerator.Configuration conf) {
        for (var cp : edgeNetworkGenerator.chron)
            if ((cp.getLeft() <= tick) && (tick <= cp.getRight()))
                return generateConfigurationForSimulationTime(cp, conf);
        return Collections.emptyList();
    }

    private List<EdgeInfrastructureGenerator.Configuration> generateConfigurationForSimulationTime(ImmutablePair<Double, Double> cp,
                                                                                                   EdgeInfrastructureGenerator.Configuration conf) {
        var css = edgeNetworkGenerator.css_in_time.get(cp);
        var edges = edgeNetworkGenerator.retrieved_basic_information.get(cp.getLeft());
        var timedNetwork = edgeNetworkGenerator.timed_connectivity.get(cp.getLeft());
        List<EdgeInfrastructureGenerator.Configuration> resultCSS = new ArrayList<>();

        for (var sub_network : css) {
            var candidate = sub_network.iterator().next();
            // Setting the remaining instance-dependant parameters
            // Cloud name
            var local = conf.copy();
            local.network_configuration.datacenter_name = "Cloud_"+candidate;
            local.edge_network_name = "Cloud_"+candidate;
            // controller name
            local.network_configuration.controller_name = "sdn_"+candidate;
            // gateway name
            local.gateway_name = "gateway_"+candidate;
            local.n_edgeDevices_and_edges = sub_network.size();

            // Setting a number of VMs which is proportional to the number given
            local.hosts_and_vms.n_vm = local.hosts_and_vms.n_hosts_per_edges * local.n_edgeDevices_and_edges;
            local.stringToInteger = new ArrayList<>(sub_network);

            for (var edge : sub_network) {
                var neigh = timedNetwork.get(edge);
                if ((neigh != null) && (!neigh.isEmpty())) {
                    local.edge_switch_network.putAll(edge, neigh);
                }
            }
            resultCSS.add(local);
        }
        return resultCSS;
    }


    public static class Configuration {
        public int numberOfClouds = 1;
        public int IoTMultiplicityForVMs = 3;
        public double global_simulation_terminate;
        public String start_time = null;
        public double start_vehicle_time;               // 0.0
        public double simulation_step = 1.0;            // 1.0
        public double end_vehicle_time;                 // 100.0
        public String iots;                             // /home/giacomo/IdeaProjects/SimulatorBridger/stats/test_vehicle.json
        public String iot_generators;                   // /home/giacomo/IdeaProjects/SimulatorBridger/iot_generators.yaml
        public String strongly_connected_components;    // /home/giacomo/IdeaProjects/SimulatorBridger/rsu.csv_timed_scc.json"
        public String edge_information;                 // /home/giacomo/IdeaProjects/SimulatorBridger/stats/test_rsu.json
        public String edge_neighbours;                  // /home/giacomo/IdeaProjects/SimulatorBridger/rsu.csv_neighboursChange.json
        public String cloud_general_configuration;      // /home/giacomo/IdeaProjects/SimulatorBridger/cloud_generators.yaml
        public String edge_general_configuration;       // /home/giacomo/IdeaProjects/SimulatorBridger/edge_generators.yaml
        public String wan_general_configuration;       // /home/giacomo/IdeaProjects/SimulatorBridger/edge_generators.yaml

        public IoTEntityGenerator first() {
            return new IoTEntityGenerator(new File(iots), new File(iot_generators));
        }

        public TimeTicker ticker() {
            return new TimeTicker(start_vehicle_time, simulation_step, end_vehicle_time);
        }

        public EdgeNetworksGenerator second() {
            return new EdgeNetworksGenerator(new File(strongly_connected_components),
                                             new File(edge_information),
                                             new File(edge_neighbours),
                                             ticker());
        }

        public CloudInfrastructureGenerator.Configuration third() {
            return YAML.parse(CloudInfrastructureGenerator.Configuration.class, new File(cloud_general_configuration)).orElseThrow();
        }

        public EdgeInfrastructureGenerator.Configuration fourth() {
            return YAML.parse(EdgeInfrastructureGenerator.Configuration.class, new File(edge_general_configuration)).orElseThrow();
        }

        public WANInfrastructureGenerator.Configuration fith() {
            return YAML.parse(WANInfrastructureGenerator.Configuration.class, new File(wan_general_configuration)).orElseThrow();
        }
    }

    public List<GlobalConfigurationSettings> getTimedPossibleConfigurations(EnsembleConfigurations.Configuration conf) {
        List<GlobalConfigurationSettings> ls = new ArrayList<>();
        List<IoTDeviceTabularConfiguration> iotDevices = ioTEntityGenerator.asIoTJSONConfigurationList();
        AtomicInteger global_program_counter = new AtomicInteger(1);
        List<WorkloadCSV> globalApps = ioTEntityGenerator.generateAppSetUp(conf.simulation_step, global_program_counter);

        for (var consistent_network_conf : edgeNetworkGenerator.simulation_intervals) {
            var filteredApps = globalApps
                    .stream()
                    .filter(x -> (consistent_network_conf.getLeft() <= x.StartDataGenerationTime_Sec) &&
                            (consistent_network_conf.getRight() >= x.StopDataGeneration_Sec))
                    .collect(Collectors.toList());

            var time = consistent_network_conf.getLeft();
            List<EdgeInfrastructureGenerator.Configuration> edgeNets = generateConfigurationForSimulationTime(time, edge);
            int nSCC = edgeNets.size();
            List<CloudInfrastructureGenerator.Configuration> cloudNets = setCloudPolicyFromIoTNumbers(conf.numberOfClouds,
                                                                                                      conf.IoTMultiplicityForVMs,
                                                                                                      nSCC,
                                                                                                      cloud);

            ls.add(ensemble(cloudNets, edgeNets, wan_conf, iotDevices, filteredApps, conf.global_simulation_terminate, conf.start_time));
        }
        return ls;
    }






    public static GlobalConfigurationSettings ensemble(List<CloudInfrastructureGenerator.Configuration> cloudNets,
                                                       List<EdgeInfrastructureGenerator.Configuration>  edgeNets,
                                                       WANInfrastructureGenerator.Configuration conf,
                                                       List<IoTDeviceTabularConfiguration> iotDevices,
                                                               List<WorkloadCSV> apps,
                                                               double terminate,
                                                               String start) {

        List<TopologyLink> global_network_links = new ArrayList<>();

        // Generating the edge nets in a way which is IoT independent, and only considering Edge devices
        List<SubNetworkConfiguration> actualEdgeDataCenters = edgeNets
                .stream()
                .map(x -> EdgeInfrastructureGenerator.generate(x, global_network_links))
                .collect(Collectors.toList());

        // Generating the cloud nets
        List<SubNetworkConfiguration> actualCloudDataCenters = cloudNets
                .stream()
                .map(x -> CloudInfrastructureGenerator.generateFromConfiguration(x, global_network_links))
                .collect(Collectors.toList());

        // Generating the sdwan_switches
        List<uk.ncl.giacomobergami.components.networking.Switch> sdwan_switches =
                WANInfrastructureGenerator.generate(cloudNets, edgeNets, conf, global_network_links);



        return new GlobalConfigurationSettings(actualEdgeDataCenters,
                actualCloudDataCenters,
                iotDevices,
                global_network_links,
                sdwan_switches,
                apps,
                conf.sdwan_traffic,
                conf.sdwan_routing,
                conf.sdwan_controller,
                terminate,
                start);
    }


    public static boolean generateConfigurationFromFile(@Input File configuration_file) {
        var conf = YAML.parse(EnsembleConfigurations.Configuration.class, configuration_file).orElseThrow();
        var ec = new EnsembleConfigurations(conf.first(), conf.second(), conf.third(), conf.fourth(), conf.fith());
        var ls = ec.getTimedPossibleConfigurations(conf);
        return true;
    }


    public static void main(String args[]) {
        generateConfigurationFromFile(new File("/home/giacomo/IdeaProjects/SimulatorBridger/inputFiles/novel/main.yaml"));
    }

}
