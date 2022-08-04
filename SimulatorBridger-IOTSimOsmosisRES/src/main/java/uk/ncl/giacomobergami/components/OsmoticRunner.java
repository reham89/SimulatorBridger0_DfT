package uk.ncl.giacomobergami.components;

import uk.ncl.giacomobergami.components.simulator.OsmoticConfiguration;
import uk.ncl.giacomobergami.components.simulator.OsmoticWrapper;
import uk.ncl.giacomobergami.utils.data.JSON;
import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.util.List;
import java.util.Optional;

public class OsmoticRunner {
    private static OsmoticWrapper obj;

    public static OsmoticWrapper generateFacade() {
        if (obj == null) {
            obj = new OsmoticWrapper();
        }
        return obj;
    }

    public static void orchestrate(String configuration) {
        List<OsmoticConfiguration> ls = JSON.stringToArray(new File(configuration), OsmoticConfiguration[].class);
        if (ls.isEmpty()) return;
        OsmoticWrapper conv = generateFacade();
        for (var y : ls) {
            conv.runConfiguration(y);
        }
        conv.stop();
        conv.log();
    }

    public static void main(String[] args) {
        String configuration = "osmotic.json";
        if (args.length >= 1) {
            configuration = args[0];
        }
        orchestrate(configuration);
    }
}
