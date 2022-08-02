package uk.ncl.giacomobergami.traffic_orchestrator;

import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.util.Optional;

public class TrafficOrchestratorRunner {
    private static Class<?> clazz;
    private static Orchestrator obj;

    public static Orchestrator generateFacade(OrchestratorConfigurator conf) {
        if (obj == null) {
            obj = new Orchestrator(conf);
        }
        return obj;
    }

    public static void orchestrate(String configuration) {
        Optional<OrchestratorConfigurator> conf = YAML.parse(OrchestratorConfigurator.class, new File(configuration));
        conf.ifPresent(x -> {
            Orchestrator conv = generateFacade(x);
            conv.run();
            conv.serializeAll();
        });
    }

    public static void main(String[] args) {
        String configuration = "orchestrator.yaml";
        if (args.length > 0) {
            configuration = args[0];
        }
        orchestrate(configuration);
    }
}
