package org.cloudbus.cloudsim.osmesis.examples.uti;

public class AccurateBatteryInformation {
    public String IoTDeviceName;
    public double time;
    public double consumption;

    public AccurateBatteryInformation() {
        IoTDeviceName = "";
        time = 0.0;
        consumption = 0.0;
    }

    public AccurateBatteryInformation(String ioTDeviceName, double time, double consume) {
        IoTDeviceName = ioTDeviceName;
        this.time = time;
        this.consumption = consume;
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
}
