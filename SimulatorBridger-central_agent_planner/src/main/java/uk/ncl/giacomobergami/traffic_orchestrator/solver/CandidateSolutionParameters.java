/*
 * CandidateSolutionParameters.java
 * This file is part of SimulatorBridger-central_agent_planner
 *
 * Copyright (C) 2022 - Giacomo Bergami
 *
 * SimulatorBridger-central_agent_planner is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * SimulatorBridger-central_agent_planner is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SimulatorBridger-central_agent_planner. If not, see <http://www.gnu.org/licenses/>.
 */


package uk.ncl.giacomobergami.traffic_orchestrator.solver;

import uk.ncl.giacomobergami.utils.algorithms.ClusterDifference;
import uk.ncl.giacomobergami.utils.shared_data.edge.TimedEdge;
import uk.ncl.giacomobergami.utils.shared_data.iot.TimedIoT;
import uk.ncl.giacomobergami.utils.structures.ImmutablePair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CandidateSolutionParameters {
    public Map<Double, LocalTimeOptimizationProblem.Solution> bestResult = null;
    public TreeMap<Double, Map<String, List<String>>> inStringTime = null;
    public HashMap<Double, Map<TimedEdge, List<TimedIoT>>> inCurrentTime = null;
    public HashMap<String, ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>>> delta_associations = null;
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

    public HashMap<String, ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>>> getDelta_associations() {
        return delta_associations;
    }

    public void setDelta_associations(HashMap<String, ImmutablePair<ImmutablePair<Double, List<String>>, List<ClusterDifference<String>>>> delta_associations) {
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
