package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NetworkGeneratorFactory {
    public static NetworkGenerator generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(NetworkGenerator.class)
                .generateFacade(clazzPath, BogusNetworkGenerator::getInstance);
    }
}
