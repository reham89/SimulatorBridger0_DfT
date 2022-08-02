package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.traffic_orchestrator.OrchestratorConfigurator;
import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class RSUUpdaterFactory {
    public static RSUUpdater generateFacade(String clazzPath, OrchestratorConfigurator configurator) {
        return ReflectiveFactoryMethod
                .getInstance(RSUUpdater.class)
                .generateFacade(clazzPath, () -> new UPdateRSUFromConfiguration(configurator), configurator);
    }
}
