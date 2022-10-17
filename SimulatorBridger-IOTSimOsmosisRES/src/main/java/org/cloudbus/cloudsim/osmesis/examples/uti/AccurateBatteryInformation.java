package org.cloudbus.cloudsim.osmesis.examples.uti;

public class AccurateBatteryInformation {
    public String IoTDeviceName;
    public double time;
    public double capacity;

    public AccurateBatteryInformation() {
        IoTDeviceName = "";
        time = 0.0;
        capacity = 0.0;
    }

    public AccurateBatteryInformation(String ioTDeviceName, double time, double capacity) {
        IoTDeviceName = ioTDeviceName;
        this.time = time;
        this.capacity = capacity;
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

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }
}
