package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.netgen;

import uk.ncl.giacomobergami.utils.design_patterns.ReflectiveFactoryMethod;

public class NetworkGeneratorFactory {
    public static NetworkGenerator generateFacade(String clazzPath) {
        return ReflectiveFactoryMethod
                .getInstance(NetworkGenerator.class)
                .generateFacade(clazzPath, BogusNetworkGenerator::getInstance);
    }
}
