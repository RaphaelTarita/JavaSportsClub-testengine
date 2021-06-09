package matrnr;

import matrnr.utils.NullChecker;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TestSuite {
    private static final NullChecker C =
        new NullChecker(new IllegalArgumentException("Test Suites do not accept null values in constructors"));

    private final String name;
    private final List<Test> tests = new ArrayList<>();

    public TestSuite(String suiteName, List<Test> inputTests) {
        name = suiteName;
        tests.addAll(C.checkAll(inputTests));
    }

    public TestSuite(String suiteName, Test... inputTests) {
        this(suiteName, Arrays.asList(C.check(inputTests)));
    }

    public String getName() {
        return name;
    }

    public List<Test> getTests() {
        return tests;
    }

    public double suiteCoverage() {
        return ((double) coveredMethods().size() + coveredConstructors().size()) / (allMethods().size() + allConstructors().size());
    }

    public Set<Class<?>> coveredClasses() {
        Set<Class<?>> allClasses = new HashSet<>();
        for (Test t : tests) {
            allClasses.addAll(t.getTestClasses());
        }
        return allClasses;
    }

    public Set<Method> allMethods() {
        Set<Method> all = new HashSet<>();
        for (Test t : tests) {
            all.addAll(t.allMethods());
        }
        return all;
    }

    public Set<Constructor<?>> allConstructors() {
        Set<Constructor<?>> all = new HashSet<>();
        for (Test t : tests) {
            all.addAll(t.allConstructors());
        }
        return all;
    }

    public Set<Method> coveredMethods() {
        Set<Method> covered = new HashSet<>();
        for (Test t : tests) {
            covered.addAll(t.coveredMethods());
        }
        return covered;
    }

    public Set<Constructor<?>> coveredConstructors() {
        Set<Constructor<?>> covered = new HashSet<>();
        for (Test t : tests) {
            covered.addAll(t.coveredConstructors());
        }
        return covered;
    }

    public Set<Method> uncoveredMethods() {
        Set<Method> all = allMethods();
        all.removeAll(coveredMethods());
        return all;
    }

    public Set<Constructor<?>> uncoveredConstructors() {
        Set<Constructor<?>> all = allConstructors();
        all.removeAll(coveredConstructors());
        return all;
    }

    public Map<Test, TestResult> test() {
        Map<Test, TestResult> results = new HashMap<>();
        for (Test t : tests) {
            results.put(t, t.test());
        }

        return results;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof TestSuite) {
            TestSuite otherTestSuite = (TestSuite) other;
            return tests.equals(otherTestSuite.tests);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tests);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TESTSUITE] ");
        sb.append(name)
            .append("\ncovered classes: [");
        for (Class<?> c : coveredClasses()) {
            sb.append(' ')
                .append(c.getName());
        }
        sb.append(" ]\noverall coverage: ")
            .append(suiteCoverage() * 100)
            .append("%\nTests: [");
        for (Test t : tests) {
            sb.append(" '")
                .append(t.getName())
                .append('\'');
        }
        sb.append(" ]");
        return sb.toString();
    }
}
