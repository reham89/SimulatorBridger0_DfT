/*
 * Title:        IoTSim-Osmosis 1.0
 * Description:  IoTSim-Osmosis enables the testing and validation of osmotic computing applications 
 * 			     over heterogeneous edge-cloud SDN-aware environments.
 * 
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2020, Newcastle University (UK) and Saudi Electronic University (Saudi Arabia) 
 * 
 */

package org.cloudbus.cloudsim.osmesis.examples;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.core.MainEventManager;
import org.cloudbus.cloudsim.edge.core.edge.LegacyConfiguration;
import org.cloudbus.cloudsim.edge.utils.LogUtil;
import org.cloudbus.cloudsim.osmesis.examples.uti.LogPrinter;
import org.cloudbus.cloudsim.osmesis.examples.uti.PrintResults;
import org.cloudbus.cloudsim.sdn.Switch;
import org.cloudbus.osmosis.core.EdgeSDNController;
import org.cloudbus.osmosis.core.OsmoticBroker;
import org.cloudbus.osmosis.core.OsmoticDatacenter;
import org.cloudbus.osmosis.core.LegacyTopologyBuilder;
import org.cloudbus.osmosis.core.OsmosisOrchestrator;
import org.cloudbus.osmosis.core.SDNController;

import org.cloudbus.osmosis.core.OsmoticAppsParser;
import uk.ncl.giacomobergami.components.mel_routing.MELRoutingPolicyGeneratorFacade;

/**
 * 
 * @author Khaled Alwasel
 * @contact kalwasel@gmail.com
 * @since IoTSim-Osmosis 1.0
 * 
**/

public class OsmesisExample_1 {
	public static final String configurationFile = "inputFiles/Example1_configuration.json";
	public static final String osmesisAppFile =  "inputFiles/Example1_Worload.csv";
//	public static final String RES_CONFIG_FILE =  "inputFiles/Example_RES_config.json";
    LegacyTopologyBuilder topologyBuilder;
	OsmoticBroker osmesisBroker;
	EdgeSDNController edgeSDNController;

	public static void main(String[] args) throws Exception {
		OsmesisExample_1 osmesis = new OsmesisExample_1();
		osmesis.start();
	}
	
	public void start() throws Exception{

		int num_user = 1; // number of users
		Calendar calendar = Calendar.getInstance();
		boolean trace_flag = false; // mean trace events

		// Initialize the CloudSim library
		MainEventManager.init(num_user, calendar, trace_flag);
		osmesisBroker  = LegacyTopologyBuilder.newBroker();
		osmesisBroker.setMelRouting(MELRoutingPolicyGeneratorFacade.generateFacade(null));
		topologyBuilder = new LegacyTopologyBuilder(osmesisBroker);
		LegacyConfiguration config = buildTopologyFromFile(configurationFile);
		//
        if(config !=  null) {
        	topologyBuilder.buildTopology(config);
        }
        
        OsmosisOrchestrator maestro = new OsmosisOrchestrator();
        
		OsmoticAppsParser.startParsingCSVAppFile(osmesisAppFile);
		List<SDNController> controllers = new ArrayList<>();
		for(OsmoticDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
			osmesisBroker.submitVmList(osmesisDC.getVmList(), osmesisDC.getId());
			controllers.add(osmesisDC.getSdnController());
			osmesisDC.getSdnController().setWanOorchestrator(maestro);			
		}
		controllers.add(topologyBuilder.getSdWanController());
		maestro.setSdnControllers(controllers);
		osmesisBroker.submitOsmesisApps(OsmoticAppsParser.appList);
		osmesisBroker.setDatacenters(topologyBuilder.getOsmesisDatacentres());

		double startTime = MainEventManager.startSimulation();
  
		LogUtil.simulationFinished();
		PrintResults pr = new PrintResults();
		pr.printOsmesisNetwork();
			
		Log.printLine();

		for(OsmoticDatacenter osmesisDC : topologyBuilder.getOsmesisDatacentres()){
			List<Switch> switchList = osmesisDC.getSdnController().getSwitchList();
			LogPrinter.printEnergyConsumption(osmesisDC.getName(), osmesisDC.getSdnhosts(), switchList, startTime);
			Log.printLine();
		}
		
		Log.printLine();		
		LogPrinter.printEnergyConsumption(topologyBuilder.getSdWanController().getName(), null, topologyBuilder.getSdWanController().getSwitchList(), startTime);		
		Log.printLine();
		Log.printLine("Simulation Finished!");

	}

	private LegacyConfiguration buildTopologyFromFile(String filePath) throws Exception {
        System.out.println("Creating topology from file " + filePath);
        LegacyConfiguration conf  = null;
		conf = topologyBuilder.buildTopology(new File(filePath));

        System.out.println("Topology built:");
        return conf;
    }
	
	public void setEdgeSDNController(EdgeSDNController edc) {
		this.edgeSDNController = edc;
	}
}
