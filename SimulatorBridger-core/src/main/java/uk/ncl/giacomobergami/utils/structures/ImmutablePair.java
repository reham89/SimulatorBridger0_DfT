package uk.ncl.giacomobergami.utils.structures;

import org.apache.commons.lang3.tuple.Pair;

public class ImmutablePair<K, V> extends Pair<K, V> {
    final K key; final V value;

    public ImmutablePair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getLeft() {
        return key;
    }

    @Override
    public V getRight() {
        return value;
    }

    @Override
    public V setValue(V value) {
        throw new UnsupportedOperationException();
    }
}
