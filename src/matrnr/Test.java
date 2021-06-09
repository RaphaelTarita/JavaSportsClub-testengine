package matrnr;

import matrnr.utils.NullChecker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Test {
    protected static final NullChecker C =
        new NullChecker(new IllegalArgumentException("Tests do not accept null values in constructors"));
    private final Set<Class<?>> testClasses;
    private final Set<Method> allMethods;
    private final Set<Constructor<?>> allConstructors;
    protected final Set<Method> testedMethods;
    protected final Set<Constructor<?>> testedConstructors;
    private final String name;
    private final String description;

    public Test(
        Set<Class<?>> classesForTesting,
        String testName,
        String testDescription,
        Set<Method> methodsForTesting,
        Set<Constructor<?>> constructorsForTesting
    ) {
        testClasses = C.checkAll(classesForTesting);
        name = C.check(testName);
        description = C.check(testDescription);

        C.checkAll(methodsForTesting);
        allMethods = testClasses.stream()
            .flatMap(c -> Arrays.stream(c.getDeclaredMethods()))
            .filter(method -> Modifier.isPublic(method.getModifiers()))
            .collect(Collectors.toSet());
        allConstructors = testClasses.stream()
            .flatMap(c -> Arrays.stream(c.getDeclaredConstructors()))
            .filter(ctor -> Modifier.isPublic(ctor.getModifiers()))
            .collect(Collectors.toSet());

        for (Method m : methodsForTesting) {
            if (!allMethods.contains(m)) {
                throw new IllegalArgumentException("Method '" + m.toString() + "' was not found in the test classes!");
            }
        }
        testedMethods = methodsForTesting;

        for (Constructor<?> c : constructorsForTesting) {
            if (!allConstructors.contains(c)) {
                throw new IllegalArgumentException("Constructor '" + c.toString() + "' was not found in the test classes!");
            }
        }
        testedConstructors = constructorsForTesting;
    }

    public Set<Class<?>> getTestClasses() {
        return testClasses;
    }

    public Set<Method> coveredMethods() {
        return testedMethods;
    }

    public Set<Method> allMethods() {
        return allMethods;
    }

    public Set<Constructor<?>> coveredConstructors() {
        return testedConstructors;
    }

    public Set<Constructor<?>> allConstructors() {
        return allConstructors;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public abstract TestResult test();

    public double testCoverage() {
        return ((double) testedMethods.size() + testedConstructors.size()) / (allMethods.size() + allConstructors.size());
    }

    public Set<Method> uncoveredMethods() {
        Set<Method> res = new HashSet<>(allMethods);
        res.removeAll(testedMethods);
        return res;
    }

    public Set<Constructor<?>> uncoveredConstructors() {
        Set<Constructor<?>> res = new HashSet<>(allConstructors);
        res.removeAll(testedConstructors);
        return res;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (other instanceof Test) {
            Test otherTest = (Test) other;
            return name.equals(otherTest.name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TEST] ");
        sb.append(name)
            .append("\nfor classes [");
        for (Class<?> tc : testClasses) {
            sb.append(' ')
                .append(tc.getName());
        }
        sb.append(" ]\ndescription: ")
            .append(description)
            .append("\ntests methods/constructors: {");

        for (Constructor<?> c : testedConstructors) {
            sb.append("\n\t- ")
                .append(c.toGenericString());
        }
        for (Method m : testedMethods) {
            sb.append("\n\t- ")
                .append(m.toGenericString());
        }
        if (!(testedMethods.isEmpty() && testedConstructors.isEmpty())) {
            sb.append('\n');
        }
        sb.append('}');
        return sb.toString();
    }
}
