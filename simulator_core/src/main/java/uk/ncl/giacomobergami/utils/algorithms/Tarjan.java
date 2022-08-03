package uk.ncl.giacomobergami.utils.algorithms;

import uk.ncl.giacomobergami.utils.structures.StraightforwardAdjacencyList;

import java.util.*;

public class Tarjan<K> {

    long index = 0;
    Stack<K> S;
    HashMap<K, Long> v_index, low_link;
    HashSet<K> onStack;
    List<List<K>> scc;

    public static <K> Map<K, Integer> asBelongingMap(List<List<K>> scc) {
        if (scc == null || scc.isEmpty()) return Collections.emptyMap();
        var map = new HashMap<K, Integer>();
        for (int i = 0, N = scc.size(); i<N; i++) {
            for (K val : scc.get(i))
                map.put(val, i);
        }
        return map;
    }

    public Tarjan() {
        S = new Stack<>();
        v_index = new HashMap<>();
        low_link = new HashMap<>();
        onStack = new HashSet<>();
        scc = new ArrayList<>();
    }

    private void init() {
        index = 0;
        S.clear();
        v_index.clear();
        low_link.clear();
        onStack.clear();
        scc.clear();
    }

    public List<List<K>> run(StraightforwardAdjacencyList<K> graph, Collection<K> vertexSet) {
        init();
        for (K vertex : vertexSet) {
            if (v_index.get(vertex) == null) {
                rec(graph, vertex);
            }
        }
        return scc;
    }

    private void rec(StraightforwardAdjacencyList<K> graph, K vertex) {
        v_index.put(vertex, index);
        low_link.put(vertex, index);
        index++;
        onStack.add(vertex);
        S.push(vertex);

        for (K w : graph.outgoing(vertex)) {
            if (v_index.get(w) == null) {
                rec(graph, w);
                low_link.put(vertex, Math.min(low_link.get(vertex), low_link.get(w)));
            } else if (onStack.contains(w)) {
                low_link.put(vertex, Math.min(low_link.get(vertex), v_index.get(w)));
            }
        }

        if (Objects.equals(low_link.get(vertex), v_index.get(vertex))) {
            List<K> new_scc = new ArrayList<>();
            K w = null;
            do {
                w = S.pop();
                onStack.remove(w);
                new_scc.add(w);
            } while (!Objects.equals(w, vertex));
            scc.add(new_scc);
        }
    }

}
