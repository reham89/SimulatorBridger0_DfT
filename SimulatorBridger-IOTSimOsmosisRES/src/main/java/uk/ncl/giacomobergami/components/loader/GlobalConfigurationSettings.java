package uk.ncl.giacomobergami.components.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.osmosis.core.LegacyTopologyBuilder;
import org.cloudbus.osmosis.core.OsmoticBroker;
import org.cloudbus.osmosis.core.SDWANController;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.components.iot.IoTDeviceTabularConfiguration;
import uk.ncl.giacomobergami.components.iot.IoTGeneratorFactory;
import uk.ncl.giacomobergami.components.networking.TopologyLink;
import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GlobalConfigurationSettings {
    public String logLevel;
    public  boolean saveLogToFile;
    public  String logFilePath;
    public  boolean append;
    public  boolean trace_flag;
    public List<String> edgeDataCenters;
    public List<String> cloudDataCenters;
    public String topologyLinksFile;
    public String IoTSpecsFile;
    public SDWAN sdwan;

    public static class SDWAN {
        public String controller_name;
        public String controllercontroller_trafficPolicy;
        public String controllercontroller_routingPolicy;
        public String switches_file;
    }

    @JsonIgnore
    public volatile File absolute;

    @JsonIgnore
    public volatile SimulatorSettings conf;

    public OsmoticBroker newBroker(String name) {
        if (conf == null)
            throw new RuntimeException("ERROR: the settins are not properly initialized!");
        return conf.newBroker(name);
    }

    @Deprecated
    public GlobalConfigurationSettings() {}

    public static GlobalConfigurationSettings readFromFile(File yaml) {
        var a = yaml.getAbsoluteFile();
        var self = YAML.parse(GlobalConfigurationSettings.class, a).orElseThrow();
        self.absolute = a;
        if (!new File(self.topologyLinksFile).isAbsolute()) {
            self.topologyLinksFile = new File(self.absolute.getParentFile(), self.topologyLinksFile).getAbsolutePath();
        }
        if (!new File(self.sdwan.switches_file).isAbsolute()) {
            self.sdwan.switches_file = new File(self.absolute.getParentFile(), self.sdwan.switches_file).getAbsolutePath();
        }
        if (!new File(self.IoTSpecsFile).isAbsolute()) {
            self.IoTSpecsFile = new File(self.absolute.getParentFile(), self.IoTSpecsFile).getAbsolutePath();
        }
        return self;
    }

    public void initLog() {
        if (saveLogToFile) {
            LogUtil.initLog(LogUtil.Level.valueOf(logLevel.toUpperCase()), logFilePath, saveLogToFile, append);
        }
    }

    public List<IoTDevice> getIoTDevices(OsmoticBroker broker) {
        List<IoTDevice> ls = new ArrayList<>();
        try (var reader = IoTDeviceTabularConfiguration.csvReader().beginCSVRead(new File(IoTSpecsFile))) {
            while (reader.hasNext()) {
                var curr = reader.next();
                IoTDevice newInstance = IoTGeneratorFactory.generateFacade(curr.asLegacyConfiguration(), conf.flowId);
                if ((curr.associatedEdge != null) && (!curr.associatedEdge.isEmpty()))
                    newInstance.setAssociatedEdge(curr.associatedEdge);
                broker.addIoTDevice(newInstance);
                ls.add(newInstance);
            }
        } catch (Exception ignored) { }
        return ls;
    }

    public List<LegacyConfiguration.SwitchEntity> asLegacySDWANSwitches() {
        var switches = new ArrayList<LegacyConfiguration.SwitchEntity>();
        try (var reader = uk.ncl.giacomobergami.components.networking.Switch.csvReader().beginCSVRead(new File(sdwan.switches_file).getAbsoluteFile())) {
            while (reader.hasNext()) {
                switches.add(reader.next().asLegacySwitchEntity(sdwan.controller_name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return switches;
    }

    public SDWANController asSDWANControllerWithNoInitializedTopology(List<Switch> datacenterGateways) {
        return new SDWANController(sdwan.controller_name, sdwan.controllercontroller_trafficPolicy, sdwan.controllercontroller_routingPolicy, datacenterGateways);
    }

    public Map<String, Collection<LegacyConfiguration.LinkEntity>> readLinksFromFile() {
        return TopologyLink.asNetworkedLinks(new File(topologyLinksFile));
    }

    public GlobalConfigurationSettings buildTopology(OsmoticBroker broker) {
        conf = new SimulatorSettings();
        var global_network_links = readLinksFromFile();

        List<Switch> datacenterGateways = new ArrayList<>();

        // Cloud Data Centers
        for (var x : cloudDataCenters) {
            var reader = new SubFolderReader(new File(absolute, x));
            if (x.equals("sdwan"))
                throw new RuntimeException("A cloud data center cannot be named 'sdwan'");
            if (reader.conf.datacenter_name.equals(x))
                throw new RuntimeException(x+" expected to be equal to "+ reader.conf.datacenter_name);
            var y = reader.createCloudDatacenter(broker, conf.hostId, conf.vmId, global_network_links);
            var controller = y.getSdnController();
            datacenterGateways.add(controller.getGateway());
            conf.osmesisDatacentres.add(y);
        }

        // Edge Data Centers
        for (var x : edgeDataCenters) {
            var reader = new SubFolderReader(new File(absolute, x));
            if (x.equals("sdwan"))
                throw new RuntimeException("An edge data center cannot be named 'sdwan'");
            if (reader.conf.datacenter_name.equals(x))
                throw new RuntimeException(x+" expected to be equal to "+ reader.conf.datacenter_name);
            var y = reader.createEdgeDatacenter(broker, conf.hostId, conf.vmId, global_network_links);
            var controller = y.getSdnController();
            datacenterGateways.add(controller.getGateway());
            conf.osmesisDatacentres.add(y);
        }

        // IoT Devices
        var iot = getIoTDevices(broker);

        // Log Initialization
        initLog();

        // WAN Network
        if  ((!global_network_links.containsKey("sdwan")) ||
                (global_network_links.get("sdwan").isEmpty())) {
            throw new RuntimeException("In order to make the simulator work, you should have a sdwan network connecting edge and cloud sub-networks!");
        }
        var sdWanController = asSDWANControllerWithNoInitializedTopology(datacenterGateways);
        sdWanController.initSdWANTopology(asLegacySDWANSwitches(),
                                          global_network_links.get("sdwan"),
                                          datacenterGateways);
        conf.osmesisDatacentres.forEach(datacenter -> datacenter.getSdnController().setWanController(sdWanController));
        sdWanController.addAllDatacenters(conf.osmesisDatacentres);
        return this;
    }
}
