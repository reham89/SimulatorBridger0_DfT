package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class NetworkGeneratorFactory {
    public static NetworkGenerator generateFacade(String clazzPath) {
        Class<?> clazz = null;
        Constructor<? extends NetworkGenerator> object = null;
        try {
            clazz = Class.forName(clazzPath);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + clazzPath);
            return BogusNetworkGenerator.getInstance();
        }
        try {
            object = (Constructor<? extends NetworkGenerator>) clazz.getConstructor();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("No valid constructor for: " + clazzPath);
            return BogusNetworkGenerator.getInstance();
        }
        try {
            return object.newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("No valid instantiation for: " + clazzPath);
            return BogusNetworkGenerator.getInstance();
        }
    }

}
