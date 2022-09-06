package uk.ncl.giacomobergami.utils.structures;

public class Union4<K,V,T,U> {
    protected K val1;
    protected V val2;
    protected T val3;
    protected U val4;
    protected int index;

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
    public U getVal4() {
        return val4;
    }
    public Object get() {
        if (index == 1)
            return val2;
        else if (index == 2)
            return val3;
        else if (index == 3)
            return val4;
        else
            return val1;
    }

    public static <K, V, T,U> Union4<K, V, T,U> left(K left) {
        Union4<K, V, T,U> var = new Union4<>();
        var.val1 = left;
        var.val2 = null;
        var.val3 = null;
        var.val4 = null;
        var.index = 0;
        return var;
    }

    public static <K, V, T,U> Union4<K, V, T,U> midLeft(V right) {
        Union4<K, V, T,U> var = new Union4<>();
        var.val1 = null;
        var.val2 = right;
        var.val3 = null;
        var.val4 = null;
        var.index = 1;
        return var;
    }

    public static <K, V, T,U> Union4<K, V, T,U> midRight(T right) {
        Union4<K, V, T,U> var = new Union4<>();
        var.val1 = null;
        var.val3 = right;
        var.val2 = null;
        var.val4 = null;
        var.index = 2;
        return var;
    }

    public static <K, V, T,U> Union4<K, V, T,U> right(U right) {
        Union4<K, V, T,U> var = new Union4<>();
        var.val1 = null;
        var.val2 = null;
        var.val3 = null;
        var.val4 = right;
        var.index = 3;
        return var;
    }

    protected Union4() {}
}
