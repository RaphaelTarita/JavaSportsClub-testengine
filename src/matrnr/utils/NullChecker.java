package matrnr.utils;

import java.util.Collection;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NullChecker {
    private final RuntimeException defaultThrow;

    public NullChecker(RuntimeException defaultIfNull) {
        Objects.requireNonNull(defaultIfNull);
        defaultThrow = defaultIfNull;
    }

    public NullChecker() {
        this(new NullPointerException());
    }

    public <T> T check(T t) {
        if (t == null) {
            throw defaultThrow;
        }
        return t;
    }

    public <T> T check(T t, RuntimeException toThrow) {
        if (t == null) {
            if (toThrow == null) {
                throw defaultThrow;
            } else {
                throw toThrow;
            }
        }
        return t;
    }

    public void checkAll(Object... os) {
        check(os);
        for (Object o : os) {
            check(o);
        }
    }

    public void checkAll(RuntimeException toThrow, Object... os) {
        check(os, toThrow);
        for (Object o : os) {
            check(o, toThrow);
        }
    }

    public <E, C extends Collection<E>> C checkAll(C elems) {
        check(elems);
        for (E elem : elems) {
            check(elem);
        }
        return elems;
    }

    public <E, C extends Collection<E>> C checkAll(C elems, RuntimeException toThrow) {
        check(elems, toThrow);
        for (E elem : elems) {
            check(elem, toThrow);
        }
        return elems;
    }

    public <T> void performChecked(T t, Consumer<T> action) {
        check(action);
        action.accept(check(t));
    }

    public <T> void performChecked(T t, RuntimeException toThrow, Consumer<T> action) {
        check(action, toThrow);
        action.accept(check(t, toThrow));
    }

    public <T, R> R computeChecked(T t, Function<T, R> action) {
        check(action);
        return action.apply(check(t));
    }

    public <T, R> R computeChecked(T t, RuntimeException toThrow, Function<T, R> action) {
        check(action, toThrow);
        return action.apply(check(t, toThrow));
    }

    public <T, U, R> R computeChecked(T t, U u, BiFunction<T, U, R> action) {
        check(action);
        return action.apply(check(t), check(u));
    }

    public <T, U, R> R computeChecked(T t, U u, RuntimeException toThrow, BiFunction<T, U, R> action) {
        check(action, toThrow);
        return action.apply(check(t, toThrow), check(u, toThrow));
    }

    public <R> R supplyChecked(Supplier<R> action) {
        check(action);
        return check(action.get());
    }

    public <R> R supplyChecked(RuntimeException toThrow, Supplier<R> action) {
        check(action, toThrow);
        return check(action.get(), toThrow);
    }
}
