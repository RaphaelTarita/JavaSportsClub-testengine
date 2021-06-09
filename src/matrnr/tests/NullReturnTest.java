package matrnr.tests;

import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.DefaultObjects;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class NullReturnTest extends Test {
    private static final String EQUALS = "equals";
    private static final String COMPARE_TO = "compareTo";
    private final Supplier<Object> testable;
    private final Class<?> subject;

    private static Set<Method> filterApplicable(Class<?> toTest, Set<String> excludes) {
        return Arrays.stream(toTest.getDeclaredMethods())
            .filter(m -> Modifier.isPublic(m.getModifiers()) && !Modifier.isStatic(m.getModifiers()))
            .filter(m -> !m.getReturnType().equals(void.class))
            .filter(m -> !excludes.contains(m.getName()))
            .collect(Collectors.toSet());
    }

    public NullReturnTest(Class<?> toTest, Set<String> excludes) {
        super(
            Set.of(toTest),
            toTest.getSimpleName() + "NullReturnChecks",
            "Checks whether 'null' values are returned from methods of " + toTest.getSimpleName(),
            filterApplicable(toTest, C.checkAll(excludes)),
            Set.of()
        );
        subject = C.check(toTest);
        testable = () -> DefaultObjects.get(toTest);
    }

    private void testReturnBehaviour(Method m, TestResult.Builder res) {
        Class<?>[] parameterTypes = m.getParameterTypes();
        List<Object> defaults = new ArrayList<>(parameterTypes.length);
        if (EQUALS.equals(m.getName()) || COMPARE_TO.equals(m.getName())) {
            defaults.add(DefaultObjects.get(subject));
        } else {
            for (Class<?> p : parameterTypes) {
                defaults.add(DefaultObjects.get(p));
            }
        }

        boolean fail = false;
        try {
            Object returnValue = m.invoke(testable.get(), defaults.toArray());
            if (returnValue == null) {
                res.failure()
                    .appendNote('\n' + m.getName() + " returned a null value");
                fail = true;
            }
        } catch (InvocationTargetException ex) {
            res.failure()
                .unexpected(ex.getCause())
                .appendNote("\nunexpected Exception caught by '" + m.getName() + '\'');
            fail = true;
        } catch (IllegalAccessException ex) {
            throw new UnsupportedOperationException("Cannot nullcheck because access was denied", ex);
        }
        if (fail && !defaults.isEmpty()) {
            res.appendNote(". Parameters used: [");
            for (Object obj : defaults) {
                res.appendNote(" '" + obj.toString() + '\'');
            }
            res.appendNote(" ]");
        }
    }

    @Override
    public TestResult test() {
        TestResult.Builder res = TestResult.builder()
            .success()
            .testedObject(testable.get());

        for (Method m : testedMethods) {
            testReturnBehaviour(m, res);
        }
        return res.build();
    }
}
