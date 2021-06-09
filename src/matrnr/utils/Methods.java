package matrnr.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Methods {
    private static final NullChecker C = new NullChecker(new IllegalArgumentException("Methods class cannot work with null values"));

    private Methods() {
    }

    private static Method[] getAllInternal(Class<?> cls, String name, Function<Class<?>, Method[]> methodSupplier) {
        C.check(cls);
        C.check(name);
        List<Method> results = new ArrayList<>();
        for (Method m : methodSupplier.apply(cls)) {
            if (name.equals(m.getName())) {
                results.add(m);
            }
        }
        return results.toArray(new Method[0]);
    }

    private static Method getAssertInternal(Class<?> cls, String name, BiFunction<Class<?>, String, Method[]> methodSupplier) {
        Method[] results = methodSupplier.apply(cls, name);
        if (results.length != 1) {
            throw new IllegalArgumentException(
                "There exist more or less than one Method with the name '"
                    + name
                    + "' in the class '"
                    + cls.getSimpleName()
                    + '\''
            );
        }
        return results[0];
    }

    private static Set<Method> getMethodsInternal(Class<?> cls, BiFunction<Class<?>, String, Method[]> methodSupplier, String[] names) {
        Set<Method> result = new HashSet<>();
        for (String name : names) {
            result.addAll(Set.of(methodSupplier.apply(cls, name)));
        }
        return result;
    }

    public static Method[] getAll(Class<?> cls, String name) {
        return getAllInternal(cls, name, Class::getDeclaredMethods);
    }

    public static Method[] getAllInTree(Class<?> cls, String name) {
        return getAllInternal(cls, name, Class::getMethods);
    }

    public static Method get(Class<?> cls, String name) {
        return getAll(cls, name)[0];
    }

    public static Method getInTree(Class<?> cls, String name) {
        return getAllInTree(cls, name)[0];
    }

    public static Method getAssert(Class<?> cls, String name) {
        return getAssertInternal(cls, name, Methods::getAll);
    }

    public static Method getInTreeAssert(Class<?> cls, String name) {
        return getAssertInternal(cls, name, Methods::getAllInTree);
    }

    public static Set<Method> getMethods(Class<?> cls, String... names) {
        return getMethodsInternal(cls, Methods::getAll, names);
    }

    public static Set<Method> getMethodsInTree(Class<?> cls, String... names) {
        return getMethodsInternal(cls, Methods::getAllInTree, names);
    }
}
