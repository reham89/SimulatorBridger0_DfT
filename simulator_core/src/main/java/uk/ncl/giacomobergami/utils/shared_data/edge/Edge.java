package uk.ncl.giacomobergami.utils.shared_data.edge;

import uk.ncl.giacomobergami.utils.shared_data.abstracted.SimulatedObject;

import java.util.HashMap;

public class Edge extends SimulatedObject<TimedEdge, EdgeProgram> {
    public Edge() { super(); }
    public Edge(HashMap<Double, TimedEdge> dynamicInformation, EdgeProgram program) {
        super(dynamicInformation, program);
    }
}
