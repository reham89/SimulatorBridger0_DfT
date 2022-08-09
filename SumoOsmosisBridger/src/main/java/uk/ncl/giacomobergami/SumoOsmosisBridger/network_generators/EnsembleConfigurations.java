package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import uk.ncl.giacomobergami.components.iot.IoTDeviceTabularConfiguration;
import uk.ncl.giacomobergami.components.loader.GlobalConfigurationSettings;
import uk.ncl.giacomobergami.components.loader.SubNetworkConfiguration;
import uk.ncl.giacomobergami.components.networking.TopologyLink;
import uk.ncl.giacomobergami.utils.asthmatic.WorkloadCSV;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EnsembleConfigurations {

    private final IoTEntityGenerator ioTEntityGenerator;

    public EnsembleConfigurations(IoTEntityGenerator ioTEntityGenerator) {
        this.ioTEntityGenerator = ioTEntityGenerator;
    }

    public void setCloudPolicyFromIoTNumbers(int numberOfClouds,
                                             int IoTMultiplicityForVMs,
                                             CloudInfrastructureGenerator.Configuration globalCloud) {

        // Assuming to set VM in the number of IT*IoTMultiplicityForVMs/numberOfClouds

    }

    public static GlobalConfigurationSettings ensemble(List<CloudInfrastructureGenerator.Configuration> cloudNets,
                                                       List<EdgeInfrastructureGenerator.Configuration>  edgeNets,
                                                       WANInfrastructureGenerator.Configuration conf,
                                                       List<IoTDeviceTabularConfiguration> iotDevices,
                                                               List<WorkloadCSV> apps,
                                                               String sdwan_traffic,
                                                               String sdwan_routing,
                                                               String sdwan_controller,
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
                sdwan_traffic,
                sdwan_routing,
                sdwan_controller,
                terminate,
                start);
    }

}
