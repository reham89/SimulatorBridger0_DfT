package uk.ncl.giacomobergami.utils.shared_data;

import java.util.HashMap;

public class Vehicle {
    public HashMap<Double, TimedVehicle> dynamicInformation;
    public VehicularProgram program;

    public Vehicle() {
        program = null;
        dynamicInformation = new HashMap<>();
    }

    public Vehicle(HashMap<Double, TimedVehicle> dynamicInformation, VehicularProgram program) {
        this.dynamicInformation = dynamicInformation;
        this.program = program;
    }

    public HashMap<Double, TimedVehicle> getDynamicInformation() {
        return dynamicInformation;
    }

    public void setDynamicInformation(HashMap<Double, TimedVehicle> dynamicInformation) {
        this.dynamicInformation = dynamicInformation;
    }

    public VehicularProgram getProgram() {
        return program;
    }

    public void setProgram(VehicularProgram program) {
        this.program = program;
    }
}
