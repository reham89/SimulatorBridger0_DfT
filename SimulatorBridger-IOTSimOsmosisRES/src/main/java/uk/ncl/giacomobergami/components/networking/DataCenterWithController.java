package uk.ncl.giacomobergami.components.networking;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.edge.core.edge.EdgeDataCenter;
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
    public String gateway_switch_name;

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
