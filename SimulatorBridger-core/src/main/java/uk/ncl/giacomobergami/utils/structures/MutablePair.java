package uk.ncl.giacomobergami.utils.structures;

import org.apache.commons.lang3.tuple.Pair;

public class MutablePair<K, V> extends Pair<K, V> {
    K key; V value;

    public MutablePair(K key, V value) {
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
        var prev = this.value;
        this.value = value;
        return prev;
    }
}
