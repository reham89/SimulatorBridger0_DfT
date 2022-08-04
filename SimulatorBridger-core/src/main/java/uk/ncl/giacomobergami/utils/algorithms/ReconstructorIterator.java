package uk.ncl.giacomobergami.utils.algorithms;

import uk.ncl.giacomobergami.utils.structures.ConcretePair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class ReconstructorIterator<H, T> implements Iterator<List<T>> {
    private final int N;
    private int i;
    private boolean beginning;
    private ConcretePair<ConcretePair<H, List<T>>, List<ClusterDifference<T>>> reconstruction;
    private final Comparator<T> cmp;
    private List<T> previousReconstruction;

    public ReconstructorIterator(ConcretePair<ConcretePair<H, List<T>>, List<ClusterDifference<T>>> reconstruction, Comparator<T> cmp) {
        this.reconstruction = reconstruction;
        this.cmp = cmp;
        this.beginning = true;
        this.N = reconstruction.getRight().size();
        previousReconstruction = null;
        i = 0;
    }

    public static <H, T> List<List<T>> reconstruct(ConcretePair<ConcretePair<H, List<T>>, List<ClusterDifference<T>>> reconstruction,
                                                   Comparator<T> cmp) {
        List<List<T>> result = new ArrayList<>(reconstruction.getRight().size()+1);
        result.add(reconstruction.getLeft().getValue());
        for (int i = 0, N = reconstruction.getRight().size(); i<N; i++) {
            result.add(reconstruction.getRight().get(i).reconstructFrom(result.get(result.size()-1), cmp));
        }
        return result;
    }

    @Override
    public boolean hasNext() {
        return beginning || (i < N);
    }

    @Override
    public List<T> next() {
        List<T> result;
        if (beginning) {
            previousReconstruction = reconstruction.getLeft().getValue();
            beginning = false;
            result = previousReconstruction;
        } else {
            var tmp = reconstruction.getRight().get(i++).reconstructFrom(previousReconstruction, cmp);
            previousReconstruction = tmp;
            result = tmp;
        }
        return result;
    }
}
