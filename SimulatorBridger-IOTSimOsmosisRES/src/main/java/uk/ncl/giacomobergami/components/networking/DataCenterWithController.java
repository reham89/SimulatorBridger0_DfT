package uk.ncl.giacomobergami.components.networking;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.osmosis.core.CloudSDNController;
import org.cloudbus.osmosis.core.EdgeSDNController;

import java.util.List;

public class DataCenterWithController {
    public double scheduling_interval;
    public String datacenter_name;
    public String datacenter_type;
    public String datacenter_vmAllocationPolicy;
    public String datacenter_architecture;
    public String datacenter_os;
    public String datacenter_vmm;
    public double datacenter_timeZone;
    public double datacenter_costPerSec;
    public double datacenter_costPerMem;
    public double datacenter_costPerStorage;
    public double datacenter_costPerBw;
    public String controller_name;
    public String controller_trafficPolicy;
    public String controller_routingPolicy;

    public DataCenterWithController() {}

    public DataCenterWithController(LegacyConfiguration.CloudDataCenterEntity cloud) {
        scheduling_interval = 0.0;
        datacenter_name = cloud.getName();
        datacenter_type = cloud.getType();
        datacenter_vmAllocationPolicy = cloud.getVmAllocationPolicy();
        datacenter_architecture = "x86";
        datacenter_os = "Linux";
        datacenter_vmm = "Xen";
        datacenter_timeZone = 10.0;
        datacenter_costPerSec = 3.0;
        datacenter_costPerMem = 0.05;
        datacenter_costPerStorage = 0.001;
        datacenter_costPerBw = 0.0;
        controller_name = cloud.getControllers().get(0).name;
        controller_trafficPolicy = cloud.getControllers().get(0).trafficPolicy;
        controller_routingPolicy = cloud.getControllers().get(0).routingPolicy;
    }

    public DataCenterWithController(LegacyConfiguration.EdgeDataCenterEntity cloud) {
        scheduling_interval = 0.0;
        datacenter_name = cloud.getName();
        datacenter_type = cloud.getType();
        datacenter_vmAllocationPolicy = cloud.getVmAllocationPolicy().getClassName();
        var features = cloud.getCharacteristics();
        datacenter_architecture = features.getArchitecture();
        datacenter_os = features.getOs();
        datacenter_vmm = features.getVmm();
        datacenter_timeZone = features.getTimeZone();
        datacenter_costPerSec = features.getCostPerSec();
        datacenter_costPerMem = features.getCostPerMem();
        datacenter_costPerStorage = features.getCostPerStorage();
        datacenter_costPerBw = features.getCostPerBw();
        controller_name = cloud.getControllers().get(0).name;
        controller_trafficPolicy = cloud.getControllers().get(0).trafficPolicy;
        controller_routingPolicy = cloud.getControllers().get(0).routingPolicy;
    }

    public CloudSDNController asCloudController() {
        return new CloudSDNController(controller_name, controller_trafficPolicy, controller_routingPolicy);
    }

    public EdgeSDNController asEdgeSDNController(EdgeDataCenter datacenter) {
        return new EdgeSDNController(controller_name, controller_trafficPolicy, controller_routingPolicy, datacenter);
    }

    public DatacenterCharacteristics asDatacenterCharacteristics(List<? extends Host> hostList) {
        return new DatacenterCharacteristics(datacenter_architecture,
                datacenter_os,
                datacenter_vmm,
                hostList,
                datacenter_timeZone,
                datacenter_costPerSec,
                datacenter_costPerMem,
                datacenter_costPerStorage,
                datacenter_costPerBw);
    }

}
