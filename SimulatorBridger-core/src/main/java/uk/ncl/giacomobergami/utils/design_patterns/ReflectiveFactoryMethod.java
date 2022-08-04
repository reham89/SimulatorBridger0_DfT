package uk.ncl.giacomobergami.utils.design_patterns;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.function.Supplier;

public class ReflectiveFactoryMethod<T> {
    private ReflectiveFactoryMethod() {}
    private static HashMap<String, ReflectiveFactoryMethod<?>> classMap = new HashMap<>();
    public static <T> ReflectiveFactoryMethod<T> getInstance(Class<? extends T> clazz) {
        if (!classMap.containsKey(clazz.getName()))
            classMap.put(clazz.getName(), new ReflectiveFactoryMethod<T>());
        return (ReflectiveFactoryMethod<T>)classMap.get(clazz.getName());
    }

    public <T> T generateFacade(String clazzPath, Supplier<T> bogus, Object... clazzez) {
        if (clazzPath == null) return bogus.get();
        Class<?> clazz = null;
        Class<?>[] actualClazzez = new Class<?>[clazzez.length];
        if (clazzPath != null) for (int i = 0; i<clazzez.length; i++) actualClazzez[i] = clazzez[i].getClass();
        Constructor<? extends T> object = null;
        try {
            clazz = Class.forName(clazzPath);
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + clazzPath);
            return bogus.get();
        }
        try {
            object = (Constructor<? extends T>) clazz.getConstructor(actualClazzez);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            System.err.println("No valid constructor for: " + clazzPath);
            return bogus.get();
        }
        try {
            return object.newInstance(clazzez);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            System.err.println("No valid instantiation for: " + clazzPath);
            return bogus.get();
        }
    }

}
