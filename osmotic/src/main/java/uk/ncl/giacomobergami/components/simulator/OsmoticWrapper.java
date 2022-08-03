package uk.ncl.giacomobergami.components.simulator;

import org.cloudbus.agent.AgentBroker;
import org.cloudbus.agent.config.AgentConfigLoader;
import org.cloudbus.agent.config.AgentConfigProvider;
import org.cloudbus.agent.config.TopologyLink;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.edge.core.edge.ConfiguationEntity;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.osmesis.examples.uti.LogPrinter;
import org.cloudbus.cloudsim.osmesis.examples.uti.PrintResults;
import org.cloudbus.cloudsim.osmesis.examples.uti.RESPrinter;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.osmosis.core.*;
import org.cloudbus.res.EnergyController;
import org.cloudbus.res.config.AppConfig;
import org.cloudbus.res.dataproviders.res.RESResponse;
import uk.ncl.giacomobergami.components.mel_routing.MELRoutingPolicy;
import uk.ncl.giacomobergami.components.mel_routing.MELRoutingPolicyGeneratorFacade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class provides an abstraction over the possible settings of running OSMOSIS
 */
public class OsmoticWrapper {
    private OsmoticConfiguration conf;
    OsmosisBuilder topologyBuilder;
    OsmesisBroker osmesisBroker;
    AgentBroker agentBroker;
    Map<String, EnergyController> energyControllers;
    private boolean init;
    private boolean started;
    private boolean finished;
    private double runTime;

    public OsmoticWrapper() {
        this(null);
    }

    public OsmoticWrapper(OsmoticConfiguration conf) {
        this.conf = conf;
        init = false;
        started = false;
        finished = false;
        energyControllers = null;
        runTime = 0.0;
    }

    private static File fileExists(String path) {
        if (path != null) {
            File n = new File(path).getAbsoluteFile();
            if (n.exists() && n.isFile()) {
                return n;
            }
        }
        return null;
    }

    public void stop() {
        if (started) {
            CloudSim.stopSimulation();
            OsmesisAppsParser.appList.clear();
            OsmesisBroker.workflowTag.clear();
            osmesisBroker = null;
            topologyBuilder = null;
            agentBroker = null;
            started = false;
            init = false;
            energyControllers = null;
            runTime = 0.0;
            started = false;
            finished = false;
        }
    }

    private ConfiguationEntity buildTopologyFromFile(String filePath) {
        System.out.println("Creating topology from file " + filePath);
        ConfiguationEntity conf  = null;
        try (FileReader jsonFileReader = new FileReader(filePath)){
            conf = topologyBuilder.parseTopology(jsonFileReader);
        } catch (FileNotFoundException e) {
            System.out.println("ERROR: input configuration file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Topology built:");
        return conf;
    }


    public boolean init(OsmoticConfiguration newConfiguration) {
        stop();
        init = false;
        this.conf = newConfiguration;
        return init();
    }

    private boolean init() {
        stop(); // ensuring that the previous simulation was stopped
        if (init) return init;
        Calendar calendar = Calendar.getInstance();

        // Getting configuration from json and entering classes to Agent Broker
        if (fileExists(conf.AGENT_CONFIG_FILE) != null) {
            // Set Agent and Message classes
            AgentBroker agentBroker = AgentBroker.getInstance();

            // Getting configuration from json and entering classes to Agent Broker
            AgentConfigProvider provider = null;
            try {
                provider = new AgentConfigProvider(AgentConfigLoader.getFromFile(conf.AGENT_CONFIG_FILE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return init;
            }

            // In this example, the Central Agent is not used
            try {
                agentBroker.setDcAgentClass(provider.getDCAgentClass());
                agentBroker.setDeviceAgentClass(provider.getDeviceAgentClass());
                agentBroker.setAgentMessageClass(provider.getAgentMessageClass());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return init;
            }

            //Simulation is not started yet thus there is not any MELs.
            //Links for Agents between infrastructure elements.
            for (TopologyLink link : provider.getTopologyLinks()) {
                agentBroker.addAgentLink(link.AgentA, link.AgentB);
            }

            //Osmotic Agents time interval
            agentBroker.setMAPEInterval(provider.getMAPEInterval());

            if (fileExists(conf.RES_CONFIG_FILE) != null) {
                RESResponse resResponse = null;
                try {
                    resResponse = AppConfig.RES_PARSER.parse(conf.RES_CONFIG_FILE);
                } catch (IOException e) {
                    e.printStackTrace();
                    return init;
                }
                energyControllers = resResponse
                        .getDatacenters()
                        .stream()
                        .map(EnergyController::fromDatacenter)
                        .collect(Collectors.toMap(EnergyController::getEdgeDatacenterId, Function.identity()));
                agentBroker.setEnergyControllers(energyControllers);
            }

            if (conf.simulationStartTime != null && (!conf.simulationStartTime.isEmpty())) {
                agentBroker.setSimulationStartTime(conf.simulationStartTime);
            }
        }

        CloudSim.init(conf.num_user, calendar, conf.trace_flag);
        if (conf.terminate_simulation_at > 0)
            CloudSim.terminateSimulation(conf.terminate_simulation_at);

        osmesisBroker  = new OsmesisBroker(conf.OsmesisBroker);
        MELRoutingPolicy melSwitchPolicy = MELRoutingPolicyGeneratorFacade.generateFacade(conf.mel_switch_policy);
        osmesisBroker.setMelRouting(melSwitchPolicy);

        topologyBuilder = new OsmosisBuilder(osmesisBroker);

        if (fileExists(conf.configurationFile) != null) {
            ConfiguationEntity config = buildTopologyFromFile(conf.configurationFile);
            if(config !=  null) {
                try {
                    topologyBuilder.buildTopology(config);
                } catch (Exception e) {
                    e.printStackTrace();
                    return init;
                }
            }
        }

        OsmosisOrchestrator conductor = new OsmosisOrchestrator();
        OsmesisAppsParser.startParsingExcelAppFile(conf.osmesisAppFile);

        List<SDNController> controllers = new ArrayList<>();
        for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
            osmesisBroker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
            controllers.add(osmesisDC.getSdnController());
            osmesisDC.getSdnController().setWanOorchestrator(conductor);
        }
        controllers.add(topologyBuilder.getSdWanController());
        conductor.setSdnControllers(controllers);
        osmesisBroker.submitOsmesisApps(OsmesisAppsParser.appList);
        osmesisBroker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

        init = true;
        return init;
    }

    public void start() {
        init(); // Ensuring that the simulation is started
        runTime = CloudSim.startSimulation();
        finished = true;
    }

    public void log() {
        if (finished) {
            LogUtil.simulationFinished();
            PrintResults pr = new PrintResults();
            pr.printOsmesisNetwork();

            Log.printLine();

            for(OsmesisDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
                List<Switch> switchList = osmesisDC.getSdnController().getSwitchList();
                LogPrinter.printEnergyConsumption(osmesisDC.getName(), osmesisDC.getSdnhosts(), switchList, runTime);
                Log.printLine();
            }

            Log.printLine();
            LogPrinter.printEnergyConsumption(topologyBuilder.getSdWanController().getName(), null, topologyBuilder.getSdWanController().getSwitchList(), runTime);
            Log.printLine();
            Log.printLine("Simulation Finished!");

            Log.printLine();
            Log.printLine("Post-mortem RES energy analysis!");

            if (energyControllers != null) {
                RESPrinter res_printer = new RESPrinter();
                res_printer.postMortemAnalysis(energyControllers, conf.simulationStartTime, true,1);
            }
            //res_printer.postMortemAnalysis(energyControllers,simulationStartTime, false, 36);
            //res_printer.postMortemAnalysis(energyControllers,"20160901:0000", false, 36);
            Log.printLine("End of RES analysis!");
        }
    }


}
