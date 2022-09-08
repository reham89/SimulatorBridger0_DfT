package uk.ncl.giacomobergami.SumoOsmosisBridger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators.EnsembleConfigurations;
import uk.ncl.giacomobergami.traffic_converter.TrafficConverterRunner;
import uk.ncl.giacomobergami.traffic_orchestrator.CentralAgentPlannerRunner;

import java.io.File;

public class MainExample {

    static {
        File file = new File("log4j2.xml");
        LoggerContext context = (LoggerContext) LogManager.getContext(false);
        context.setConfigLocation(file.toURI());
    }

    public static void main(String args[]) {
        String converter = "clean_example/1_traffic_information_collector_configuration/converter.yaml";
        String orchestrator = "clean_example/2_central_agent_oracle_configuration/orchestrator.yaml";
        String simulator_runner = "clean_example/3_extIOTSim_configuration/main.yaml";
        if (args.length >= 2) {
            converter = args[0];
            orchestrator = args[1];
        }
//        TrafficConverterRunner.convert(converter);
//        CentralAgentPlannerRunner.orchestrate(orchestrator, converter);
        EnsembleConfigurations.runConfigurationFromFile(simulator_runner);
    }

}
