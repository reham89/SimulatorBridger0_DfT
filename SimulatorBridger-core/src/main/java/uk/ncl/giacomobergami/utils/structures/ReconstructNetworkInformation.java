package uk.ncl.giacomobergami.utils.structures;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.algorithms.ReconstructorIterator;
import uk.ncl.giacomobergami.utils.algorithms.StringComparator;
import uk.ncl.giacomobergami.utils.shared_data.edge.Edge;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;

import java.util.*;

public class ReconstructNetworkInformation implements Iterator<ReconstructNetworkInformation.TimedNetwork> {

    private final TreeMap<Double, List<List<String>>> timed_scc;
    Iterator<Map.Entry<Double, List<List<String>>>> timed_scc_iterator;
    HashMap<String, Edge> edgeNodeForReconstruction;
    HashMap<String, ReconstructorIterator<Double, String>> reconstructorIteratorHashMap;

    public HashMap<String, Edge> getEdgeNodeForReconstruction() {
        return edgeNodeForReconstruction;
    }
    public void setEdgeNodeForReconstruction(HashMap<String, Edge> edgeNodeForReconstruction) {
        this.edgeNodeForReconstruction = edgeNodeForReconstruction;
    }

    public static class TimedNetwork {
        public StraightforwardAdjacencyList<TimedEdge> network;
        public ArrayList<TimedEdge> tls;
        public HashMap<String, TimedEdge> rsuProgramHashMap;
        public SetMultimap<TimedEdge, TimedEdge> edgeToSCC;
    }

    public ReconstructNetworkInformation(HashMap<String, ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>>> adjacencyListVariationInTime,
                                         TreeMap<Double, List<List<String>>> timed_scc,
                                         HashMap<String, Edge> edgeNodeForReconstruction) {
        this.timed_scc = timed_scc;
        this.timed_scc_iterator = this.timed_scc.entrySet().iterator();
        this.edgeNodeForReconstruction = edgeNodeForReconstruction;
        this.reconstructorIteratorHashMap = new HashMap<>();
        for (var x : adjacencyListVariationInTime.entrySet()) {
            reconstructorIteratorHashMap.put(x.getKey(), new ReconstructorIterator<>(x.getValue(), StringComparator.getInstance()));
        }
    }


    @Override
    public boolean hasNext() {
        return timed_scc_iterator.hasNext();
    }

    public TimedEdge reconstructTimeEdge(String edgeId, double tick) {
        return edgeNodeForReconstruction.get(edgeId).dynamicInformation.get(tick);
    }

    @Override
    public TimedNetwork next() {
        var cp = timed_scc_iterator.next();
        var tick = cp.getKey();
        var result = new TimedNetwork();
        var rsuses = new HashSet<TimedEdge>();

        // Getting the SCCs
        result.rsuProgramHashMap = new HashMap<>();
        result.edgeToSCC = HashMultimap.create();
        for (var scc : cp.getValue()) {
            for (String id : scc) {
                var dst1 = result.rsuProgramHashMap.computeIfAbsent(id, s -> reconstructTimeEdge(s, tick));
                for (String id2 : scc) {
                    var dst2 = result.rsuProgramHashMap.computeIfAbsent(id2, s -> reconstructTimeEdge(s, tick));
                    result.edgeToSCC.put(dst1, dst2);
                }
            }
        }

        // Reconstructing the network
        result.network = new StraightforwardAdjacencyList<>();
        for (var cp2 : reconstructorIteratorHashMap.entrySet()) {
            var rsu = cp2.getKey();
            TimedEdge src = reconstructTimeEdge(rsu, tick);
            rsuses.add(src);
            for (var dstIdStr : cp2.getValue().next()) {
                TimedEdge dst = reconstructTimeEdge(dstIdStr, tick);
                result.network.put(src, dst);
                rsuses.add(dst);
            }
        }

        // Reconstructing the set of available edges.
        result.tls = new ArrayList<>(rsuses);

        return result;
    }
}
