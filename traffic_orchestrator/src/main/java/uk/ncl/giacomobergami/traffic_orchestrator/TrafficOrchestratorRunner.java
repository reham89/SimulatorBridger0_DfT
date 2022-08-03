package uk.ncl.giacomobergami.traffic_orchestrator;

import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.pipeline_confs.OrchestratorConfiguration;
import uk.ncl.giacomobergami.utils.pipeline_confs.TrafficConfiguration;

import java.io.File;
import java.util.Optional;

public class TrafficOrchestratorRunner {
    private static Class<?> clazz;
    private static Orchestrator obj;

    public static Orchestrator generateFacade(OrchestratorConfiguration conf,
                                              TrafficConfiguration conf2) {
        if (obj == null) {
            obj = new Orchestrator(conf, conf2);
        }
        return obj;
    }

    public static void orchestrate(String configuration,
                                   String conf2) {
        Optional<OrchestratorConfiguration> conf = YAML.parse(OrchestratorConfiguration.class, new File(configuration));
        Optional<TrafficConfiguration> conf3 = YAML.parse(TrafficConfiguration.class, new File(conf2));
        conf.ifPresent(x -> conf3.ifPresent(y -> {
            Orchestrator conv = generateFacade(x, y);
            conv.run();
            conv.serializeAll();
        }));
    }

    public static void main(String[] args) {
        String configuration = "orchestrator.yaml";
        String converter = "orchestrator.yaml";
        if (args.length >= 2) {
            configuration = args[0];
            converter = args[1];
        }
        orchestrate(configuration, converter);
    }
}
