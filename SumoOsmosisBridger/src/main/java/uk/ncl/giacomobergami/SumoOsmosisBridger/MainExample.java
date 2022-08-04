package uk.ncl.giacomobergami.SumoOsmosisBridger;

import uk.ncl.giacomobergami.traffic_converter.TrafficConverterRunner;
import uk.ncl.giacomobergami.traffic_orchestrator.CentralAgentPlannerRunner;

public class MainExample {

    public static void main(String args[]) {
        String converter = "converter.yaml";
        String orchestrator = "orchestrator.yaml";
        if (args.length >= 2) {
            converter = args[0];
            orchestrator = args[1];
        }
        TrafficConverterRunner.convert(converter);
        CentralAgentPlannerRunner.orchestrate(orchestrator, converter);
    }

}
