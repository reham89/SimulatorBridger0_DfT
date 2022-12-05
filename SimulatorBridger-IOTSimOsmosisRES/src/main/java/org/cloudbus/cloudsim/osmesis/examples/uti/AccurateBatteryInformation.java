package org.cloudbus.cloudsim.osmesis.examples.uti;

public class AccurateBatteryInformation {
    public String IoTDeviceName;
    public double time;
    public double consumption;
    public double noPackets;

    public AccurateBatteryInformation() {
        IoTDeviceName = "";
        time = 0.0;
        consumption = 0.0;
        noPackets = 0.0;
    }

    public AccurateBatteryInformation(String ioTDeviceName, double time, double consume, long communicate) {
        IoTDeviceName = ioTDeviceName;
        this.time = time;
        this.consumption = consume;
        this.noPackets = communicate;
    }

    public String getIoTDeviceName() {
        return IoTDeviceName;
    }

    public void setIoTDeviceName(String ioTDeviceName) {
        IoTDeviceName = ioTDeviceName;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public double getConsumption() {
        return consumption;
    }

    public void setConsumption(double consumption) {
        this.consumption = consumption;
    }

    public double getNoPackets() {
        return noPackets;
    }

    public void setNoPackets(double noPackets) {
        this.noPackets = noPackets;
    }
}
