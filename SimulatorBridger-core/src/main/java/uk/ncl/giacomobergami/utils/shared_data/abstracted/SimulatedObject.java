package uk.ncl.giacomobergami.utils.shared_data.abstracted;

import java.util.HashMap;

public class SimulatedObject<T extends TimedObject<?>, Program extends SimulationProgram> {

    public HashMap<Double, T> dynamicInformation;
    public Program program;

    public SimulatedObject() {
        program = null;
        dynamicInformation = new HashMap<>();
    }

    public SimulatedObject(HashMap<Double, T> dynamicInformation, Program program) {
        this.dynamicInformation = dynamicInformation;
        this.program = program;
    }

    public HashMap<Double, T> getDynamicInformation() { return dynamicInformation; }

    public void setDynamicInformation(HashMap<Double, T> dynamicInformation) {
        this.dynamicInformation = dynamicInformation;
    }

    public Program getProgram() { return program; }
    public void setProgram(Program program) { this.program = program; }

}
