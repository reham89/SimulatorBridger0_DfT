package uk.ncl.giacomobergami.components;

import uk.ncl.giacomobergami.components.simulator.OsmoticConfiguration;
import uk.ncl.giacomobergami.components.simulator.OsmoticWrapper;
import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.util.Optional;

public class OsmoticRunner {
    private static OsmoticWrapper obj;

    public static OsmoticWrapper generateFacade(OsmoticConfiguration conf) {
        if (obj == null) {
            obj = new OsmoticWrapper(conf);
        }
        return obj;
    }

    public static void orchestrate(String configuration) {
        Optional<OsmoticConfiguration> conf = YAML.parse(OsmoticConfiguration.class, new File(configuration));
        conf.ifPresent(y -> {
            OsmoticWrapper conv = generateFacade(y);
            conv.init();
            conv.start();
            conv.stop();
            conv.log();
        });
    }

    public static void main(String[] args) {
        String configuration = "osmotic.yaml";
        if (args.length >= 1) {
            configuration = args[0];
        }
        orchestrate(configuration);
    }
}
