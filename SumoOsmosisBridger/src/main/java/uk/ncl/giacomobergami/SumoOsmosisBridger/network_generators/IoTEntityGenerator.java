package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import uk.ncl.giacomobergami.components.iot.IoTDevice;
import uk.ncl.giacomobergami.components.iot.IoTDeviceTabularConfiguration;
import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.shared_data.iot.IoT;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class IoTEntityGenerator {
    final TreeMap<String, IoT>  timed_scc;
    final IoTGlobalConfiguration conf;

    public static class IoTGlobalConfiguration {
        public String networkType;
        public String communicationProtocol;
        public double bw;
        public double max_battery_capacity;
        public double battery_sensing_rate;
        public double battery_sending_rate;
        public String ioTClassName;

        public String getNetworkType() {
            return networkType;
        }
        public void setNetworkType(String networkType) {
            this.networkType = networkType;
        }
        public String getCommunicationProtocol() {
            return communicationProtocol;
        }
        public void setCommunicationProtocol(String communicationProtocol) {
            this.communicationProtocol = communicationProtocol;
        }
        public double getBw() {
            return bw;
        }
        public void setBw(double bw) {
            this.bw = bw;
        }
        public double getMax_battery_capacity() {
            return max_battery_capacity;
        }
        public void setMax_battery_capacity(double max_battery_capacity) {
            this.max_battery_capacity = max_battery_capacity;
        }
        public double getBattery_sensing_rate() {
            return battery_sensing_rate;
        }
        public void setBattery_sensing_rate(double battery_sensing_rate) {
            this.battery_sensing_rate = battery_sensing_rate;
        }
        public double getBattery_sending_rate() {
            return battery_sending_rate;
        }
        public void setBattery_sending_rate(double battery_sending_rate) {
            this.battery_sending_rate = battery_sending_rate;
        }
        public String getIoTClassName() {
            return ioTClassName;
        }
        public void setIoTClassName(String ioTClassName) {
            this.ioTClassName = ioTClassName;
        }
    }

    public IoTEntityGenerator(TreeMap<String, IoT> timed_scc,
                              IoTGlobalConfiguration conf) {
        this.timed_scc = timed_scc;
        this.conf = conf;
    }

    public IoTEntityGenerator(File iotFiles, File configuration) {
        Type sccType = new TypeToken<TreeMap<String, IoT>>() {}.getType();
        conf = YAML.parse(IoTGlobalConfiguration.class, configuration).orElseThrow();
        Gson gson = new Gson();
        BufferedReader reader1 = null;
        try {
            reader1 = new BufferedReader(new FileReader(iotFiles.getAbsoluteFile()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }
        timed_scc = gson.fromJson(reader1, sccType);
        try {
            reader1.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void updateIoTDevice(IoTDevice toUpdateWithTime,
                                double simTimeLow,
                                double simTimeUp) {
        var ls = timed_scc.get(toUpdateWithTime.getName());
        var times = new TreeSet<>(ls.dynamicInformation.keySet());
        var expectedLow = times.lower(simTimeLow);
        var dist = simTimeUp - simTimeLow;
        if ((expectedLow != null) && (expectedLow <= simTimeUp)) {
            var expObj = ls.dynamicInformation.get(expectedLow);
            toUpdateWithTime.mobility.range.beginX = (int) (toUpdateWithTime.mobility.location.x = expObj.x);
            toUpdateWithTime.mobility.range.beginY = (int) (toUpdateWithTime.mobility.location.y = expObj.y);
            toUpdateWithTime.mobility.location.y = ls.dynamicInformation.get(expectedLow).y;
            Double expectedUp = simTimeUp + dist;
            expectedUp = times.lower(expectedUp);
            if (expectedUp != null) {
                expObj = ls.dynamicInformation.get(expectedUp);
                toUpdateWithTime.mobility.range.endX = (int) expObj.x;
                toUpdateWithTime.mobility.range.endY = (int) expObj.y;
            }
        }
    }

    public int maximumNumberOfCommunicatingVehicles() {
        return timed_scc.size();
    }

    public List<IoTDeviceTabularConfiguration> asIoTJSONConfigurationList() {
        return timed_scc.values()
                .stream()
                .map(x -> {
                    var ls = new TreeSet<>(x.dynamicInformation.keySet());
                    var firstTime = ls.first();
                    ls.remove(firstTime);
                    var min = x.dynamicInformation.get(firstTime);
                    var iot = new IoTDeviceTabularConfiguration();
                    iot.beginX = (int) min.x;
                    iot.beginY = (int) min.y;
                    iot.movable = ls.size() > 0;
                    if (iot.movable) {
                        iot.hasMovingRange = true;
                        var nextTime = ls.first();
                        var minNext = x.dynamicInformation.get(nextTime);
                        iot.endX = (int) minNext.x;
                        iot.endY = (int) minNext.y;
                    }
                    iot.associatedEdge = null;
                    iot.networkType = conf.networkType;
                    iot.velocity = min.speed;
                    iot.name = min.id;
                    iot.communicationProtocol = conf.communicationProtocol;
                    iot.bw = conf.bw;
                    iot.max_battery_capacity = conf.max_battery_capacity;
                    iot.battery_sensing_rate = conf.battery_sensing_rate;
                    iot.battery_sending_rate = conf.battery_sending_rate;
                    iot.ioTClassName = conf.ioTClassName;
                    return iot;
                }).collect(Collectors.toList());
    }

    public static void main(String[] args) {
        new IoTEntityGenerator(new File("/home/giacomo/IdeaProjects/SimulatorBridger/stats/test_vehicle.json"),
                new File("/home/giacomo/IdeaProjects/SimulatorBridger/iot_generators.yaml"));
    }
}
