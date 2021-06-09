package matrnr;

import matrnr.utils.NullChecker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestResult {
    private static final NullChecker C = new NullChecker(new IllegalArgumentException("TestResults do not accept null values in constructors"));

    public static class Builder {
        private boolean result;
        private List<Throwable> unexpectedExceptions = new ArrayList<>();
        private List<Throwable> expectedExceptions = new ArrayList<>();
        private String notes;
        private Collection<Object> testedObjects = new ArrayList<>();

        private Builder() {
            result = false;
            notes = "";
        }

        public Builder success() {
            result = true;
            return this;
        }

        public Builder failure() {
            result = false;
            return this;
        }

        public Builder result(boolean testResult) {
            result = testResult;
            return this;
        }

        public Builder unexpectedExceptions(List<Throwable> unexpected) {
            unexpectedExceptions = unexpected;
            return this;
        }

        public Builder unexpected(Throwable t) {
            unexpectedExceptions.add(t);
            return this;
        }

        public Builder expectedExceptions(List<Throwable> expected) {
            expectedExceptions = expected;
            return this;
        }

        public Builder expected(Throwable t) {
            expectedExceptions.add(t);
            return this;
        }

        public Builder notes(String additionalNotes) {
            notes = additionalNotes;
            return this;
        }

        public Builder appendNote(String note) {
            notes += note;
            return this;
        }

        public Builder testedObjects(Collection<Object> objects) {
            testedObjects = objects;
            return this;
        }

        public Builder testedObject(Object object) {
            testedObjects.add(object);
            return this;
        }

        public TestResult build() {
            return new TestResult(
                result,
                unexpectedExceptions,
                expectedExceptions,
                notes,
                testedObjects
            );
        }
    }

    private final Collection<Object> testedObjects;
    private final boolean success;
    private final List<Throwable> unexpectedExceptions;
    private final List<Throwable> expectedExceptions;
    private final String notes;

    private TestResult(
        boolean result,
        List<Throwable> unexpected,
        List<Throwable> expected,
        String additionalNotes,
        Collection<Object> objects
    ) {
        success = result;
        unexpectedExceptions = unexpected == null ? new ArrayList<>() : C.checkAll(unexpected);
        expectedExceptions = expected == null ? new ArrayList<>() : C.checkAll(expected);
        notes = C.check(additionalNotes);
        testedObjects = objects == null ? new ArrayList<>() : C.checkAll(objects);
    }

    public static Builder builder() {
        return new Builder();
    }

    public boolean successful() {
        return success;
    }

    public List<Throwable> getUnexpectedExceptions() {
        return unexpectedExceptions;
    }

    public List<Throwable> getExpectedExceptions() {
        return expectedExceptions;
    }

    public String getNotes() {
        return notes;
    }

    public Collection<Object> getTestedObj() {
        return testedObjects;
    }

    public Builder thisBuilder() {
        return builder()
            .result(success)
            .unexpectedExceptions(unexpectedExceptions)
            .expectedExceptions(expectedExceptions)
            .notes(notes)
            .testedObjects(testedObjects);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Result: [");

        sb.append(success ? "SUCCESS" : "FAILURE")
            .append(']');
        if (!unexpectedExceptions.isEmpty()) {
            sb.append("\nThe following unexpected exceptions occurred:");
            for (Throwable t : unexpectedExceptions) {
                sb.append("\n\t- ")
                    .append(t);
            }
        }
        if (!expectedExceptions.isEmpty()) {
            sb.append("\nThe following expected exceptions occurred:");
            for (Throwable t : expectedExceptions) {
                sb.append("\n\t- ")
                    .append(t);
            }
        }
        if (!notes.isBlank()) {
            sb.append("\nAdditional notes: ")
                .append(notes);
        }
        if (!testedObjects.isEmpty()) {
            sb.append("\nDump of tested objects:");
            for (Object obj : testedObjects) {
                sb.append("\n\t- ")
                    .append(obj);
            }
        }
        return sb.toString();
    }
}
