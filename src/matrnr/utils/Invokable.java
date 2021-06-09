package matrnr.utils;

import java.lang.reflect.InvocationTargetException;

@FunctionalInterface
public interface Invokable {
    void invoke(Object[] args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException;
}
