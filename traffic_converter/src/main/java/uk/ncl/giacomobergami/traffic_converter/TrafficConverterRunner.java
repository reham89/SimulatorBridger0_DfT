package uk.ncl.giacomobergami.traffic_converter;

import uk.ncl.giacomobergami.traffic_converter.abstracted.TrafficConverter;
import uk.ncl.giacomobergami.utils.data.YAML;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class TrafficConverterRunner {

    public static class MainConverterConfiguration {
        public String clazzPath;
        public String YAMLConverterConfiguration;
        public String RSUCsvFile;
        public String VehicleCsvFile;
        public long begin, end, step;

        public long getBegin() {
            return begin;
        }

        public void setBegin(long begin) {
            this.begin = begin;
        }

        public long getEnd() {
            return end;
        }

        public void setEnd(long end) {
            this.end = end;
        }

        public long getStep() {
            return step;
        }

        public void setStep(long step) {
            this.step = step;
        }

        public String getClazzPath() {
            return clazzPath;
        }

        public void setClazzPath(String clazzPath) {
            this.clazzPath = clazzPath;
        }

        public String getYAMLConverterConfiguration() {
            return YAMLConverterConfiguration;
        }

        public void setYAMLConverterConfiguration(String YAMLConverterConfiguration) {
            this.YAMLConverterConfiguration = YAMLConverterConfiguration;
        }

        public String getRSUCsvFile() {
            return RSUCsvFile;
        }

        public void setRSUCsvFile(String RSUCsvFile) {
            this.RSUCsvFile = RSUCsvFile;
        }

        public String getVehicleCsvFile() {
            return VehicleCsvFile;
        }

        public void setVehicleCsvFile(String vehicleCsvFile) {
            VehicleCsvFile = vehicleCsvFile;
        }
    }

    private static Class<?> clazz;
    private static Constructor<? extends TrafficConverter> object;

    public static TrafficConverter generateFacade(MainConverterConfiguration conf) {
        if (clazz == null) {
            try {
                clazz = Class.forName(conf.clazzPath);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found: " + conf.clazzPath);
                System.exit(1);
            }
        }
        if (object == null) {
            try {
                object = (Constructor<? extends TrafficConverter>) clazz.getConstructor(String.class, String.class, String.class);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
                System.err.println("No valid constructor for: " + conf.clazzPath);
                System.exit(1);
            }
        }
        try {
            return (TrafficConverter)object.newInstance(conf.YAMLConverterConfiguration, conf.RSUCsvFile, conf.VehicleCsvFile);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("No valid instantiation for: " + conf.clazzPath);
            System.exit(1);
            return null;
        }
    }

    public static void convert(String configuration) {
        Optional<MainConverterConfiguration> conf = YAML.parse(MainConverterConfiguration.class, new File(configuration));
        conf.ifPresent(x -> {
            TrafficConverter conv = generateFacade(x);
            conv.runSimulator(x.begin, x.end, x.step);
            conv.dumpOrchestratorConfiguration();
        });
    }

    public static void main(String[] args) {
        String configuration = "converter.yaml";
        if (args.length > 0) {
            configuration = args[0];
        }
        convert(configuration);
    }
}
