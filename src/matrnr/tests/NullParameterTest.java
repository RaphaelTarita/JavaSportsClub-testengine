package matrnr.tests;

import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.DefaultObjects;
import matrnr.utils.Invokable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class NullParameterTest extends Test {
    private final Set<Class<? extends Throwable>> allowed;

    private final Supplier<Object> testable;

    private static <T extends Executable> Set<T> filterApplicable(Class<?> toTest, Function<Class<?>, T[]> getter, Set<String> excludes) {
        return Arrays.stream(getter.apply(toTest))
            .filter(e -> Modifier.isPublic(e.getModifiers()))
            .filter(e -> Arrays.stream(e.getParameterTypes()).anyMatch(t -> !t.isPrimitive()))
            .filter(e -> !excludes.contains(e.getName()))
            .collect(Collectors.toSet());
    }

    public NullParameterTest(Class<?> toTest, Set<Class<? extends Throwable>> allowedExceptions, Set<String> excludes) {
        super(
            Set.of(toTest),
            toTest.getSimpleName() + "NullParameterChecks",
            "Checks whether 'null' input parameters are handled properly in methods and constructors of " + toTest.getSimpleName(),
            filterApplicable(toTest, Class::getDeclaredMethods, C.checkAll(excludes)),
            filterApplicable(toTest, Class::getDeclaredConstructors, C.checkAll(excludes))
        );
        allowed = C.checkAll(allowedExceptions);
        C.check(toTest);
        testable = () -> DefaultObjects.get(toTest);
    }

    private void testNullBehaviour(Executable exec, Invokable inv, TestResult.Builder res) {
        List<Object> parameterMask = new ArrayList<>();
        for (Class<?> p : exec.getParameterTypes()) {
            if (!p.isPrimitive()) {
                parameterMask.add(null);
            } else {
                parameterMask.add(DefaultObjects.get(p));
            }
        }

        try {
            inv.invoke(parameterMask.toArray());
            res.appendNote("\nCalled '" + exec.getName() + "' with null value but did not throw exception (may be a problem)");
        } catch (InvocationTargetException ex) {
            Throwable targetExc = ex.getCause();
            if (allowed.stream().anyMatch(cls -> cls.isInstance(targetExc))) {
                res.expected(targetExc);
            } else {
                res.failure()
                    .unexpected(targetExc)
                    .appendNote("\nunexpected Exception caught by '" + exec.getName() + '\'');
            }
            if (targetExc instanceof NullPointerException) {
                res.appendNote("\nNullPointerException caught by '" + exec.getName() + '\'');
            }
        } catch (InstantiationException ex) {
            throw new UnsupportedOperationException("Cannot nullcheck because constructor invocation failed", ex);
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException("Cannot nullcheck because access was denied", ex);
        }
    }

    @Override
    public TestResult test() {
        TestResult.Builder res = TestResult.builder()
            .success()
            .testedObject(testable.get());

        for (Constructor<?> c : testedConstructors) {
            testNullBehaviour(c, c::newInstance, res);
        }
        for (Method m : testedMethods) {
            testNullBehaviour(m, params -> m.invoke(testable.get(), params), res);
        }
        return res.build();
    }
}
