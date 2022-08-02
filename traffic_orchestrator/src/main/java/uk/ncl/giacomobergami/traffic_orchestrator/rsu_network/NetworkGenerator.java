package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network;

import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.List;
import java.util.function.Function;

public interface NetworkGenerator extends Function<List<RSU>, StraightforwardAdjacencyList<RSU>> {
}
