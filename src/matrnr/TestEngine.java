package matrnr;

import matrnr.utils.NullChecker;
import matrnr.utils.Pair;
import matrnr.utils.TablePrinter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class TestEngine {
    private static final NullChecker C = new NullChecker(new IllegalArgumentException("TestResults do not accept null values in constructors"));
    private static final int REPORT_WIDTH = 79;
    private static final int REPORT_PADDING = 34;
    private static final int STATS_PADDING = 25;
    private static final String SYS_NEWLINE = System.lineSeparator();

    private static String newline() {
        return SYS_NEWLINE;
    }

    private static String newlines(int n) {
        return String.valueOf(SYS_NEWLINE).repeat(n);
    }

    public static class Builder {
        private PrintStream overviewOutput;
        private Path fullOutput;
        private final Set<TestSuite> suites;
        private final Set<Test> isolatedTests;

        private Builder() {
            overviewOutput = System.out;
            fullOutput = null;
            suites = new HashSet<>();
            isolatedTests = new HashSet<>();
        }

        public Builder overviewOutput(PrintStream stream) {
            overviewOutput = stream;
            return this;
        }

        public Builder fullOutput(Path path) {
            fullOutput = path;
            return this;
        }

        public Builder registerSuite(TestSuite suite) {
            suites.add(suite);
            return this;
        }

        public Builder registerSuites(Collection<TestSuite> testSuites) {
            suites.addAll(testSuites);
            return this;
        }

        public Builder registerTest(Test test) {
            isolatedTests.add(test);
            return this;
        }

        public Builder registerTests(Collection<Test> tests) {
            isolatedTests.addAll(tests);
            return this;
        }

        public Builder registerAllAsIsolatedTests(TestSuite suite) {
            isolatedTests.addAll(suite.getTests());
            return this;
        }

        public TestEngine build() {
            return new TestEngine(overviewOutput, fullOutput, suites, isolatedTests);
        }
    }

    private final PrintStream overviewOutput;
    private final Path fullOutput;
    private final Set<TestSuite> suites;
    private final Set<Test> isolatedTests;
    private final TablePrinter printer;

    private TestEngine(PrintStream overviewOutput, Path fullOutput, Set<TestSuite> suites, Set<Test> isolatedTests) {
        this.overviewOutput = C.check(overviewOutput);
        this.fullOutput = fullOutput; // nullable
        this.suites = C.checkAll(suites);
        this.isolatedTests = C.checkAll(isolatedTests);
        this.printer = new TablePrinter(overviewOutput);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Set<TestSuite> getSuites() {
        return suites;
    }

    public Set<Test> getIsolatedTests() {
        return isolatedTests;
    }

    public Set<Class<?>> coveredClasses() {
        Set<Class<?>> covered = new HashSet<>();
        for (TestSuite ts : suites) {
            covered.addAll(ts.coveredClasses());
        }
        for (Test t : isolatedTests) {
            covered.addAll(t.getTestClasses());
        }
        return covered;
    }

    public Set<Method> allMethods() {
        Set<Method> all = new HashSet<>();
        for (TestSuite ts : suites) {
            all.addAll(ts.allMethods());
        }
        for (Test t : isolatedTests) {
            all.addAll(t.allMethods());
        }
        return all;
    }

    public Set<Constructor<?>> allConstructors() {
        Set<Constructor<?>> all = new HashSet<>();
        for (TestSuite ts : suites) {
            all.addAll(ts.allConstructors());
        }
        for (Test t : isolatedTests) {
            all.addAll(t.allConstructors());
        }
        return all;
    }

    public Set<Method> coveredMethods() {
        Set<Method> covered = new HashSet<>();
        for (TestSuite ts : suites) {
            covered.addAll(ts.coveredMethods());
        }
        for (Test t : isolatedTests) {
            covered.addAll(t.coveredMethods());
        }
        return covered;
    }

    public Set<Constructor<?>> coveredConstructors() {
        Set<Constructor<?>> covered = new HashSet<>();
        for (TestSuite ts : suites) {
            covered.addAll(ts.coveredConstructors());
        }
        for (Test t : isolatedTests) {
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

    public double totalCoverage() {
        return ((double) coveredMethods().size() + coveredConstructors().size()) / (allMethods().size() + allConstructors().size());
    }

    public Pair<Map<TestSuite, Map<Test, TestResult>>, Map<Test, TestResult>> test() {
        Map<TestSuite, Map<Test, TestResult>> suiteResults = new HashMap<>();
        Map<Test, TestResult> isolatedResults = new HashMap<>();

        for (TestSuite ts : suites) {
            suiteResults.put(ts, ts.test());
        }
        for (Test t : isolatedTests) {
            isolatedResults.put(t, t.test());
        }
        Pair<Map<TestSuite, Map<Test, TestResult>>, Map<Test, TestResult>> cumulated = new Pair<>(suiteResults, isolatedResults);
        printResults(cumulated);
        return cumulated;
    }

    private void printResults(Pair<Map<TestSuite, Map<Test, TestResult>>, Map<Test, TestResult>> results) {
        printOverview(results);
        if (fullOutput != null) {
            overviewOutput.println("\nFull test output was written to: " + fullOutput);
            printFull(results);
        }
    }

    private void printOverview(Pair<Map<TestSuite, Map<Test, TestResult>>, Map<Test, TestResult>> results) {
        Collection<TestResult> allResults = results.first()
            .values()
            .stream()
            .flatMap(map -> map.values().stream())
            .collect(Collectors.toList());
        allResults.addAll(results.second().values());

        long totalSuccesses = allResults.stream().filter(TestResult::successful).count();
        long totalFailures = allResults.stream().filter(tr -> !tr.successful()).count();
        long totalTests = totalSuccesses + totalFailures;

        printer.printHeading(REPORT_WIDTH, "PERFORMED TEST ROUTINE, RESULTS BELOW")
            .printHeading(REPORT_WIDTH, "OVERVIEW")
            .printTableRow(REPORT_PADDING, "[Suite]", "[SUCCESS]", "[FAILURE]")
            .printEmptyLine()
            .printTableRow(REPORT_PADDING, "total", Long.toString(totalSuccesses), Long.toString(totalFailures))
            .printHeading(REPORT_WIDTH, "DETAIL REPORT")
            .printTableRow(REPORT_PADDING, "[Suite]", "[SUCCESS]", "[FAILURE]")
            .printEmptyLine();
        for (Map.Entry<TestSuite, Map<Test, TestResult>> suiteResult : results.first().entrySet()) {
            long suiteSuccesses = suiteResult.getValue().values().stream().filter(TestResult::successful).count();
            long suiteFailures = suiteResult.getValue().values().stream().filter(tr -> !tr.successful()).count();
            printer.printTableRow(REPORT_PADDING, suiteResult.getKey().getName(), Long.toString(suiteSuccesses), Long.toString(suiteFailures));
        }
        long isolatedSuccesses = results.second().values().stream().filter(TestResult::successful).count();
        long isolatedFailures = results.second().values().stream().filter(tr -> !tr.successful()).count();
        printer.printTableRow(REPORT_PADDING, "isolated tests", Long.toString(isolatedSuccesses), Long.toString(isolatedFailures))
            .printHeading(REPORT_WIDTH, "STATS")
            .printEmptyLine()
            .printTableRow(STATS_PADDING, "Total tests performed:", Long.toString(totalTests))
            .printTableRow(STATS_PADDING, "Total succeeded (%):", Double.toString((totalSuccesses * 100.0) / totalTests) + '%')
            .printTableRow(STATS_PADDING, "Total failed (%):", Double.toString((totalFailures * 100.0) / totalTests) + '%')
            .printTableRow(STATS_PADDING, "Total coverage (%):", Double.toString(totalCoverage() * 100) + '%');

        if (totalFailures > 0) {
            Set<Map.Entry<Test, TestResult>> allTests = results.first()
                .values()
                .stream()
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toSet());
            allTests.addAll(results.second().entrySet());

            Set<Test> allFailed = allTests.stream()
                .filter(entry -> !entry.getValue().successful())
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

            overviewOutput.println();
            overviewOutput.println();
            overviewOutput.print("Failed Tests: [");
            for (Test t : allFailed) {
                overviewOutput.print(' ');
                overviewOutput.print(t.getName());
            }
            overviewOutput.print(" ]");
        }
    }

    private void printFull(Pair<Map<TestSuite, Map<Test, TestResult>>, Map<Test, TestResult>> results) {
        try (BufferedWriter writer = Files.newBufferedWriter(
            fullOutput,
            StandardCharsets.UTF_8,
            StandardOpenOption.WRITE,
            StandardOpenOption.CREATE,
            StandardOpenOption.TRUNCATE_EXISTING
        )) {
            writer.append("==== TEST RESULTS ====");
            for (Map.Entry<TestSuite, Map<Test, TestResult>> topEntry : results.first().entrySet()) {
                writer.append(newline());
                for (Map.Entry<Test, TestResult> nestedEntry : topEntry.getValue().entrySet()) {
                    writer.append(newline());
                    writer.append('(')
                        .append(topEntry.getKey().getName())
                        .append(") ")
                        .append(nestedEntry.getKey().getName())
                        .append(':')
                        .append(newline())
                        .append(nestedEntry.getValue().toString());
                }
            }
            writer.append(newline());
            for (Map.Entry<Test, TestResult> entry : results.second().entrySet()) {
                writer.append(newline())
                    .append("(isolated) ")
                    .append(entry.getKey().getName())
                    .append(':')
                    .append(newline())
                    .append(entry.getValue().toString());
            }

            writer.append(newlines(3))
                .append("==== OVERVIEW OF TEST SUITES AND TESTS ====")
                .append(newline());
            for (Map.Entry<TestSuite, Map<Test, TestResult>> topEntry : results.first().entrySet()) {
                writer.append(topEntry.getKey().toString())
                    .append(newline())
                    .append("Test details:");
                for (Map.Entry<Test, TestResult> nestedEntry : topEntry.getValue().entrySet()) {
                    writer.append(newline())
                        .append(nestedEntry.getKey().toString());
                }
                writer.append(newlines(2));
            }

            if (!results.second().isEmpty()) {
                writer.append("Isolated Tests:");
                for (Map.Entry<Test, TestResult> entry : results.second().entrySet()) {
                    writer.append(newline())
                        .append(entry.getKey().toString());
                }
            }
        } catch (IOException ex) {
            System.err.println("Could not write full output to defined path '" + fullOutput + "', exception: " + ex);
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other instanceof TestEngine) {
            TestEngine otherTestEngine = (TestEngine) other;
            return suites.equals(otherTestEngine.suites) && isolatedTests.equals(otherTestEngine.isolatedTests);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(suites, isolatedTests);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[TESTENGINE]\n");
        sb.append("covered classes: [");
        for (Class<?> c : coveredClasses()) {
            sb.append(' ')
                .append(c.getName());
        }

        sb.append("total coverage: ")
            .append(totalCoverage() * 100)
            .append("%\nTest Suites: [");
        for (TestSuite ts : suites) {
            sb.append(" '")
                .append(ts.getName())
                .append('\'');
        }

        sb.append(" ]\nIsolated Tests: [");
        for (Test t : isolatedTests) {
            sb.append(' ')
                .append(t.getName());
        }

        sb.append(" ]");
        return sb.toString();
    }
}
