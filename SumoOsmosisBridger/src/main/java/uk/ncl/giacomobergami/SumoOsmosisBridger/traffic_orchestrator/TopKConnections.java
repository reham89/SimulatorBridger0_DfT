package uk.ncl.giacomobergami.SumoOsmosisBridger.traffic_orchestrator;

import com.eatthepath.jvptree.VPTree;
import uk.ncl.giacomobergami.traffic_orchestrator.rsu_network.NetworkGenerator;
import uk.ncl.giacomobergami.utils.data.YAML;
import uk.ncl.giacomobergami.utils.gir.SquaredCartesianDistanceFunction;
import uk.ncl.giacomobergami.utils.shared_data.RSU;
import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class TopKConnections implements NetworkGenerator {

    SquaredCartesianDistanceFunction dist = SquaredCartesianDistanceFunction.getInstance();
    private TopKConnectionsConfiguration conf;

    public TopKConnections() {
        conf = YAML.parse(TopKConnectionsConfiguration.class, new File("TopKConnections.yaml").getAbsoluteFile()).orElseThrow();
    }

    @Override
    public StraightforwardAdjacencyList<RSU> apply(List<RSU> rsus) {
        var tree = new VPTree<>(dist, rsus);
        StraightforwardAdjacencyList<RSU> result = new StraightforwardAdjacencyList<>();
        for (var x : rsus) {
            List<RSU> adj;
            if (conf.squaredDistance > 0.0) {
                adj = tree.getAllWithinDistance(x, conf.squaredDistance);
            } else if (conf.top_k > 0) {
                adj = tree.getNearestNeighbors(x, conf.top_k+1);
            } else {
                adj = tree.getNearestNeighbors(x, 4);
            }
            if (adj != null) {
                for (var next : adj) {
                    if (!Objects.equals(x, next)) result.put(x, next);
                }
            }
        }
        return result;
    }
}
