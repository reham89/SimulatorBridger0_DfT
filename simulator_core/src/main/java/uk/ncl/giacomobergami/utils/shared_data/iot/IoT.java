package uk.ncl.giacomobergami.utils.shared_data.iot;

import uk.ncl.giacomobergami.utils.shared_data.abstracted.SimulatedObject;

import java.util.HashMap;

public class IoT extends SimulatedObject<TimedIoT, IoTProgram> {
    public IoT() { super(); }
    public IoT(HashMap<Double, TimedIoT> dynamicInformation, IoTProgram program) {
        super(dynamicInformation, program);
    }
}
