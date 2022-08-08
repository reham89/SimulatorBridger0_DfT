package uk.ncl.giacomobergami.components.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.osmosis.core.OsmoticBroker;
import org.cloudbus.osmosis.core.SDWANController;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.components.iot.IoTDeviceTabularConfiguration;
import uk.ncl.giacomobergami.components.iot.IoTGeneratorFactory;
import uk.ncl.giacomobergami.components.networking.DataCenterWithController;
import uk.ncl.giacomobergami.components.networking.Host;
import uk.ncl.giacomobergami.components.networking.TopologyLink;
import uk.ncl.giacomobergami.components.networking.VM;
import uk.ncl.giacomobergami.components.simulator.OsmoticConfiguration;
import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

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
    public String apps_file;
    public String OsmesisBroker;
    public int num_user;
    public String mel_switch_policy;
    public double terminate_simulation_at;
    public String simulationStartTime;
    public String RES_CONFIG_FILE;
    public String AGENT_CONFIG_FILE;

    public OsmoticConfiguration asPreviousOsmoticConfiguration() {
        OsmoticConfiguration conf = new OsmoticConfiguration();
        conf.AGENT_CONFIG_FILE = AGENT_CONFIG_FILE;
        conf.RES_CONFIG_FILE = RES_CONFIG_FILE;
        conf.simulationStartTime = simulationStartTime;
        conf.terminate_simulation_at = terminate_simulation_at;
        conf.mel_switch_policy = mel_switch_policy;
        conf.num_user = num_user;
        conf.OsmesisBroker = OsmesisBroker;
        conf.osmesisAppFile = apps_file;
        conf.trace_flag = trace_flag;
        return conf;
    }

    public static class SDWAN {
        public String controller_name;
        public String controllercontroller_trafficPolicy;
        public String controllercontroller_routingPolicy;
        public String switches_file;

        public String getController_name() {
            return controller_name;
        }

        public void setController_name(String controller_name) {
            this.controller_name = controller_name;
        }

        public String getControllercontroller_trafficPolicy() {
            return controllercontroller_trafficPolicy;
        }

        public void setControllercontroller_trafficPolicy(String controllercontroller_trafficPolicy) {
            this.controllercontroller_trafficPolicy = controllercontroller_trafficPolicy;
        }

        public String getControllercontroller_routingPolicy() {
            return controllercontroller_routingPolicy;
        }

        public void setControllercontroller_routingPolicy(String controllercontroller_routingPolicy) {
            this.controllercontroller_routingPolicy = controllercontroller_routingPolicy;
        }

        public String getSwitches_file() {
            return switches_file;
        }

        public void setSwitches_file(String switches_file) {
            this.switches_file = switches_file;
        }
    }

    @JsonIgnore
    public volatile File absolute;

    @JsonIgnore
    public volatile SimulatorSettings conf;

    @JsonIgnore
    public volatile SDWANController sdWanController;

    public OsmoticBroker newBroker() {
        if (conf == null)
            throw new RuntimeException("ERROR: the settins are not properly initialized!");
        return conf.newBroker(OsmesisBroker);
    }

    public void fromLegacyConfiguration(LegacyConfiguration conf) {

        // Dumping the complete graph of the network, so to better analyse and visualize it
        topologyLinksFile = "network.csv";
        apps_file = "apps.csv";
        sdwan = new SDWAN();
        OsmesisBroker = "OsmesisBroker";
        mel_switch_policy = "uk.ncl.giacomobergami.components.mel_routing.RoundRobinMELRoutingPolicy";
        num_user = 1;
        terminate_simulation_at = -1.0;
        try (var writer = TopologyLink.csvReader().beginCSVWrite(new File(topologyLinksFile))) {
            for (var cloud : conf.getCloudDatacenter()) {
                var network = cloud.getName();
                File folder = new File(network);
                if (folder.exists() && (!folder.isDirectory())) {
                    throw new RuntimeException("ERROR: make "+network+" as a folder in the current directory");
                } else if (!folder.exists()) {
                    folder.mkdirs();
                }
                var hosts = cloud.getHosts().stream().map(Host::new).collect(Collectors.toList());
                var vms_or_mels = cloud.getVMs().stream().map(VM::new).collect(Collectors.toList());
                var switches = cloud.getSwitches().stream().map(uk.ncl.giacomobergami.components.networking.Switch::new).collect(Collectors.toList());
                var dataForFolder = new SubFolderForNetwork(hosts, vms_or_mels, switches, new DataCenterWithController(cloud));
                dataForFolder.serializeToFolder(folder);
                for (var legacyLink : cloud.getLinks()) {
                    writer.write(new TopologyLink(network, legacyLink));
                }
            }
            for (var edge : conf.getEdgeDatacenter()) {
                var network = edge.getName();
                File folder = new File(network);
                if (folder.exists() && (!folder.isDirectory())) {
                    throw new RuntimeException("ERROR: make "+network+" as a folder in the current directory");
                } else if (!folder.exists()) {
                    folder.mkdirs();
                }
                var hosts = edge.getHosts().stream().map(Host::new).collect(Collectors.toList());
                var vms_or_mels = edge.getMELEntities().stream().map(VM::new).collect(Collectors.toList());
                var switches = edge.getSwitches().stream().map(uk.ncl.giacomobergami.components.networking.Switch::new).collect(Collectors.toList());
                var dataForFolder = new SubFolderForNetwork(hosts, vms_or_mels, switches, new DataCenterWithController(edge));
                dataForFolder.serializeToFolder(folder);
                for (var legacyLink : edge.getLinks()) {
                    writer.write(new TopologyLink(network, legacyLink));
                }
            }
            for (var wan : conf.getSdwan()) {
                String network = "sdwan";
                sdwan.switches_file = "sdwan_switches.csv";
                sdwan.controller_name = wan.getControllers().name;
                sdwan.controllercontroller_routingPolicy = wan.getControllers().routingPolicy;
                sdwan.controllercontroller_trafficPolicy = wan.getControllers().trafficPolicy;
                for (var legacyLink : wan.getLinks()) {
                    writer.write(new TopologyLink(network, legacyLink));
                }
                try (var wswitch = uk.ncl.giacomobergami.components.networking.Switch.csvReader().beginCSVWrite(new File(sdwan.switches_file))) {
                    for (var legacySwitch : wan.getSwitches()) {
                        wswitch.write(new uk.ncl.giacomobergami.components.networking.Switch(legacySwitch));
                    }
                } catch (Exception e) {}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Dumping all of the IoT Devices, so that they are not exclusevly attached to a network
        IoTSpecsFile = "iot.csv";
        try (var writer = IoTDeviceTabularConfiguration.csvReader().beginCSVWrite(new File(IoTSpecsFile))) {
            for (var cloud : conf.getEdgeDatacenter()) {
                for (var legacyLink : cloud.getIoTDevices()) {
                    writer.write(IoTDeviceTabularConfiguration.fromLegacy(legacyLink));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        trace_flag = conf.isTrace_flag();

        var logger = conf.getLogEntity();
        logLevel = logger.getLogLevel();
        saveLogToFile = logger.isSaveLogToFile();
        logFilePath = logger.getLogFilePath();
        append = logger.isAppend();

        edgeDataCenters = new ArrayList<>();
        conf.getEdgeDatacenter().forEach( x-> edgeDataCenters.add(x.getName()));
        cloudDataCenters = new ArrayList<>();
        conf.getCloudDatacenter().forEach(x -> cloudDataCenters.add(x.getName()));

        YAML.serialize(this, new File("iot_sim_osmosis_res.yaml").getAbsoluteFile());
    }

    @Deprecated
    public GlobalConfigurationSettings() {
        conf = new SimulatorSettings();
    }

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
        if (!new File(self.apps_file).isAbsolute()) {
            self.apps_file = new File(self.absolute.getParentFile(), self.apps_file).getAbsolutePath();
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

        var global_network_links = readLinksFromFile();

        List<Switch> datacenterGateways = new ArrayList<>();

        // Cloud Data Centers
        for (var x : cloudDataCenters) {
            var reader = new SubFolderForNetwork(new File(absolute.getParentFile(), x));
            if (x.equals("sdwan"))
                throw new RuntimeException("A cloud data center cannot be named 'sdwan'");
            if (!reader.conf.datacenter_name.equals(x))
                throw new RuntimeException(x+" expected to be equal to "+ reader.conf.datacenter_name);
            var y = reader.createCloudDatacenter(broker, conf.hostId, conf.vmId, global_network_links);
            var controller = y.getSdnController();
            datacenterGateways.add(controller.getGateway());
            conf.osmesisDatacentres.add(y);
        }

        // Edge Data Centers
        for (var x : edgeDataCenters) {
            var reader = new SubFolderForNetwork(new File(absolute.getParentFile(), x));
            if (x.equals("sdwan"))
                throw new RuntimeException("An edge data center cannot be named 'sdwan'");
            if (!reader.conf.datacenter_name.equals(x))
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
        sdWanController = asSDWANControllerWithNoInitializedTopology(datacenterGateways);
        sdWanController.initSdWANTopology(asLegacySDWANSwitches(),
                                          global_network_links.get("sdwan"),
                                          datacenterGateways);
        conf.osmesisDatacentres.forEach(datacenter -> datacenter.getSdnController().setWanController(sdWanController));
        sdWanController.addAllDatacenters(conf.osmesisDatacentres);
        return this;
    }

    public String getLogLevel() {
        return logLevel;
    }

    public void setLogLevel(String logLevel) {
        this.logLevel = logLevel;
    }

    public boolean isSaveLogToFile() {
        return saveLogToFile;
    }

    public void setSaveLogToFile(boolean saveLogToFile) {
        this.saveLogToFile = saveLogToFile;
    }

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public boolean isAppend() {
        return append;
    }

    public void setAppend(boolean append) {
        this.append = append;
    }

    public boolean isTrace_flag() {
        return trace_flag;
    }

    public void setTrace_flag(boolean trace_flag) {
        this.trace_flag = trace_flag;
    }

    public List<String> getEdgeDataCenters() {
        return edgeDataCenters;
    }

    public void setEdgeDataCenters(List<String> edgeDataCenters) {
        this.edgeDataCenters = edgeDataCenters;
    }

    public List<String> getCloudDataCenters() {
        return cloudDataCenters;
    }

    public void setCloudDataCenters(List<String> cloudDataCenters) {
        this.cloudDataCenters = cloudDataCenters;
    }

    public String getTopologyLinksFile() {
        return topologyLinksFile;
    }

    public void setTopologyLinksFile(String topologyLinksFile) {
        this.topologyLinksFile = topologyLinksFile;
    }

    public String getIoTSpecsFile() {
        return IoTSpecsFile;
    }

    public void setIoTSpecsFile(String ioTSpecsFile) {
        IoTSpecsFile = ioTSpecsFile;
    }

    public SDWAN getSdwan() {
        return sdwan;
    }

    public void setSdwan(SDWAN sdwan) {
        this.sdwan = sdwan;
    }

    public String getApps_file() {
        return apps_file;
    }

    public void setApps_file(String apps_file) {
        this.apps_file = apps_file;
    }

    public String getOsmesisBroker() {
        return OsmesisBroker;
    }

    public void setOsmesisBroker(String osmesisBroker) {
        OsmesisBroker = osmesisBroker;
    }

    public int getNum_user() {
        return num_user;
    }

    public void setNum_user(int num_user) {
        this.num_user = num_user;
    }

    public String getMel_switch_policy() {
        return mel_switch_policy;
    }

    public void setMel_switch_policy(String mel_switch_policy) {
        this.mel_switch_policy = mel_switch_policy;
    }

    public double getTerminate_simulation_at() {
        return terminate_simulation_at;
    }

    public void setTerminate_simulation_at(double terminate_simulation_at) {
        this.terminate_simulation_at = terminate_simulation_at;
    }

    public String getSimulationStartTime() {
        return simulationStartTime;
    }

    public void setSimulationStartTime(String simulationStartTime) {
        this.simulationStartTime = simulationStartTime;
    }

    public String getRES_CONFIG_FILE() {
        return RES_CONFIG_FILE;
    }

    public void setRES_CONFIG_FILE(String RES_CONFIG_FILE) {
        this.RES_CONFIG_FILE = RES_CONFIG_FILE;
    }

    public String getAGENT_CONFIG_FILE() {
        return AGENT_CONFIG_FILE;
    }

    public void setAGENT_CONFIG_FILE(String AGENT_CONFIG_FILE) {
        this.AGENT_CONFIG_FILE = AGENT_CONFIG_FILE;
    }

    public File getAbsolute() {
        return absolute;
    }

    public void setAbsolute(File absolute) {
        this.absolute = absolute;
    }

    public SimulatorSettings getConf() {
        return conf;
    }

    public void setConf(SimulatorSettings conf) {
        this.conf = conf;
    }
}
