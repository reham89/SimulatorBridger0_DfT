package uk.ncl.giacomobergami.components;

import uk.ncl.giacomobergami.components.loader.GlobalConfigurationSettings;
import uk.ncl.giacomobergami.components.simulator.OsmoticConfiguration;
import uk.ncl.giacomobergami.components.simulator.OsmoticWrapper;
import uk.ncl.giacomobergami.utils.data.JSON;

import java.io.File;
import java.util.List;

public class OsmoticRunner {
    private static OsmoticWrapper obj;

    public static OsmoticWrapper generateFacade() {
        if (obj == null) {
            obj = new OsmoticWrapper();
        }
        return obj;
    }

    public static void legacyOrchestrate(String configuration) {
        List<OsmoticConfiguration> ls = JSON.stringToArray(new File(configuration), OsmoticConfiguration[].class);
        if (ls.isEmpty()) return;
        OsmoticWrapper conv = generateFacade();
        for (var y : ls) {
            conv.runConfiguration(y);
        }
        conv.stop();
        conv.legacy_log();
    }

    public static void current(String configuration) {
        var conf = GlobalConfigurationSettings.readFromFile(new File(configuration));
        var conv = new OsmoticWrapper(conf.asPreviousOsmoticConfiguration());
        conv.runConfiguration(conf);
        conv.stop();
        conv.log(conf);
    }

    public static void main(String[] args) {
        String configuration = "/home/giacomo/IdeaProjects/SimulatorBridger/inputFiles/original/iot_sim_osmosis_res.yaml";
        if (args.length >= 1) {
            configuration = args[0];
        }
current(configuration);
        //legacyOrchestrate("osmotic.json");
    }
}
