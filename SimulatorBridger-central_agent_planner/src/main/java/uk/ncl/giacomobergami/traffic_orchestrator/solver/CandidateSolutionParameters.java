package uk.ncl.giacomobergami.traffic_orchestrator.solver;

import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;
import uk.ncl.giacomobergami.utils.shared_data.iot.TimedIoT;
import uk.ncl.giacomobergami.utils.structures.ConcretePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CandidateSolutionParameters {
    public Map<Double, LocalTimeOptimizationProblem.Solution> bestResult = null;
    public TreeMap<Double, Map<String, List<String>>> inStringTime = null;
    public HashMap<Double, Map<TimedEdge, List<TimedIoT>>> inCurrentTime = null;
    public HashMap<String, ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>>> delta_associations = null;
    public long networkingRankingTime;
    public Double bestResultScore;

    public Map<Double, LocalTimeOptimizationProblem.Solution> getBestResult() {
        return bestResult;
    }

    public void setBestResult(Map<Double, LocalTimeOptimizationProblem.Solution> bestResult) {
        this.bestResult = bestResult;
    }

    public TreeMap<Double, Map<String, List<String>>> getInStringTime() {
        return inStringTime;
    }

    public void setInStringTime(TreeMap<Double, Map<String, List<String>>> inStringTime) {
        this.inStringTime = inStringTime;
    }

    public HashMap<Double, Map<TimedEdge, List<TimedIoT>>> getInCurrentTime() {
        return inCurrentTime;
    }

    public void setInCurrentTime(HashMap<Double, Map<TimedEdge, List<TimedIoT>>> inCurrentTime) {
        this.inCurrentTime = inCurrentTime;
    }

    public HashMap<String, ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>>> getDelta_associations() {
        return delta_associations;
    }

    public void setDelta_associations(HashMap<String, ConcretePair<ConcretePair<Double, List<String>>, List<ClusterDifference<String>>>> delta_associations) {
        this.delta_associations = delta_associations;
    }

    public long getNetworkingRankingTime() {
        return networkingRankingTime;
    }

    public void setNetworkingRankingTime(long networkingRankingTime) {
        this.networkingRankingTime = networkingRankingTime;
    }

    public Double getBestResultScore() {
        return bestResultScore;
    }

    public void setBestResultScore(Double bestResultScore) {
        this.bestResultScore = bestResultScore;
    }
}
