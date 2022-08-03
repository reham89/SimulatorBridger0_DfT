package uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.netgen;

import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface NetworkGenerator extends Function<Collection<RSU>, StraightforwardAdjacencyList<RSU>> {
}
