package uk.ncl.giacomobergami.SumoOsmosisBridger.network_generators.from_traffic_data;

import uk.ncl.giacomobergami.utils.structures.ImmutablePair;
import uk.ncl.giacomobergami.utils.structures.MutablePair;

import java.util.ArrayList;
import java.util.List;

public class TimeTicker {

    private final double begin;
    private final double end;
    private final double step;
    List<ImmutablePair<Double, Double>> traffic_simulation_ticks;

    public TimeTicker(double begin,
                      double end,
                      double step) {
        traffic_simulation_ticks = new ArrayList<>((int)Math.ceil((end-begin)/step));
        this.begin = begin;
        this.end = end;
        this.step = step;
        double current = begin;
        while (current < end) {
            traffic_simulation_ticks.add(new ImmutablePair<>(current, current+step));
            current += step;
        }
    }

    public List<ImmutablePair<Double, Double>> getChron() {
        return traffic_simulation_ticks;
    }

    private List<Integer> scatteredIntervals(double low, double high) {
        if (low > high)
            return scatteredIntervals(high, low);
        List<Integer> ls = new ArrayList<>();
        boolean findMin = true;

        for (int i = 0, traffic_simulation_ticksSize = traffic_simulation_ticks.size(); i < traffic_simulation_ticksSize; i++) {
            ImmutablePair<Double, Double> pair = traffic_simulation_ticks.get(i);
            if (findMin) {
                if (pair.getLeft() <= low) {
                    if (pair.getRight() > low) {
                        ls.add(i);
                        findMin = false;
                    }
                }
            } else {
                if (pair.getLeft() >= high)
                    return ls;
                if (high < pair.getLeft())
                    return ls;
                else if (high <= pair.getRight()) {
                    ls.add(i);
                    return ls;
                } else
                    ls.add(i);
            }
        }
        return ls;
    }

    public boolean fitsAll(double low, double high) {
        return scatteredIntervals(low, high).size() == traffic_simulation_ticks.size();
    }

    public ImmutablePair<Double, Double> reconstructIntervals(double low, double high) {
        var ls = scatteredIntervals(low, high);
        if (ls.isEmpty())
            return null;
        else
            return new ImmutablePair<>(traffic_simulation_ticks.get(ls.get(0)).getLeft(), traffic_simulation_ticks.get(ls.get(ls.size()-1)).getRight());
    }

    public static List<MutablePair<Double, Double>> mergeIntervals(List<ImmutablePair<Double, Double>> ls) {
        if (ls.isEmpty()) {
            return new ArrayList<>();
        } else if (ls.size() == 1) {
            var result = new ArrayList<MutablePair<Double, Double>>();
            var pair = ls.get(0);
            if (pair != null) {
                result.add(new MutablePair<>(pair.getLeft(), pair.getRight()));
            }
            return result;
        } else {
            List<MutablePair<Double, Double>> results = new ArrayList<>();
            int i = 0;
            while (i < ls.size()) {
                var currentPair = ls.get(i);
                if (currentPair != null) {
                    var current = new MutablePair<>(currentPair.getLeft(), currentPair.getRight());
                    i++;
                    while ((i < ls.size()) && (ls.get(i) != null) && (ls.get(i).getLeft().equals(current.getRight()))) {
                        current.setValue(ls.get(i).getRight());
                        i++;
                    }
                    results.add(current);
                } else {
                    i++;
                }
            }
            return results;
        }
    }



    /*
    * Commented out for testing
    * Added same method above with modification and null handling
    public static List<MutablePair<Double, Double>> mergeIntervals(List<ImmutablePair<Double, Double>> ls) {
        if (ls.size() <= 1) {
            var result = new ArrayList<MutablePair<Double, Double>>();
            result.add(new MutablePair<>(ls.get(0).getLeft(), ls.get(0).getRight()));
            return result;
        }
        else {
            List<MutablePair<Double, Double>> results  = new ArrayList<>();
            int i = 0;
            while (i<ls.size()) {
                var current = new MutablePair<>(ls.get(i).getLeft(), ls.get(i).getRight());
                i++;
                while ((i<ls.size()) && (ls.get(i).getLeft().equals(current.getRight()))) {
                    current.setValue(ls.get(i++).getRight());
                }
                results.add(current);
            }
            return results;
        }
    }
*/
    public double getBegin() {
        return begin;
    }
    public double getEnd() {
        return end;
    }
    public double getStep() {
        return step;
    }
}
