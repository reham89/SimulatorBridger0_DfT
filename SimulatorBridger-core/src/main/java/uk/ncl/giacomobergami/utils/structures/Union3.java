package uk.ncl.giacomobergami.utils.structures;

public class Union3<K,V,T> {
    K val1;
    V val2;
    T val3;
    int index;

    public int index() {
        return index;
    }
    public K getVal1() {
        return val1;
    }
    public V getVal2() {
        return val2;
    }
    public T getVal3() {
        return val3;
    }
    public Object get() {
        if (index == 1)
            return val2;
        else if (index == 2)
            return val3;
        else
            return val1;
    }

    public static <K, V, T> Union3<K, V, T> left(K left) {
        Union3<K, V, T> var = new Union3<>();
        var.val1 = left;
        var.val2 = null;
        var.val3 = null;
        var.index = 0;
        return var;
    }

    public static <K, V, T> Union3<K, V, T> mid(V right) {
        Union3<K, V, T> var = new Union3<>();
        var.val1 = null;
        var.val2 = right;
        var.val3 = null;
        var.index = 1;
        return var;
    }

    public static <K, V, T> Union3<K, V, T> right(T right) {
        Union3<K, V, T> var = new Union3<>();
        var.val1 = null;
        var.val2 = null;
        var.val3 = right;
        var.index = 2;
        return var;
    }

    private Union3() {}
}
