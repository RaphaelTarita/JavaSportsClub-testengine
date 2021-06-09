package matrnr.tests;

import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class InternalStructureCheck extends Test {
    private final Class<?> clazz;
    private final int expectedFieldCount;
    private final Map<String, Predicate<Field>> filters;

    public InternalStructureCheck(Class<?> forCheck, int expectedInstanceFieldCount, Map<String, Predicate<Field>> fieldFilters) {
        super(
            Set.of(C.check(forCheck)),
            forCheck.getSimpleName() + "InternalStructureCheck",
            "Checks whether the internal field structure of the " + forCheck.getSimpleName() + " class conforms to the specification",
            Set.of(),
            Set.of()
        );
        clazz = forCheck;
        expectedFieldCount = expectedInstanceFieldCount;
        filters = fieldFilters;
    }

    private void checkFieldCount(Set<Field> all, List<String> errors, List<String> warnings) {
        long fieldCount = all.stream()
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .count();

        if (fieldCount != expectedFieldCount) {
            warnings.add(fieldCount + " instance fields found (expected " + expectedFieldCount + ')');
        }
    }

    private void checkField(Map.Entry<String, Predicate<Field>> filter, Set<Field> all, List<String> errors, List<String> warnings) {
        Set<Field> fieldCandidates = all.stream()
            .filter(f -> !Modifier.isStatic(f.getModifiers()))
            .filter(filter.getValue())
            .collect(Collectors.toSet());

        if (fieldCandidates.size() < 1) {
            errors.add("No field for storing '" + filter.getKey() + "' found");
        } else if (fieldCandidates.size() == 1) {
            String fieldName = fieldCandidates.iterator().next().getName();
            if (!filter.getKey().equals(fieldName)) {
                warnings.add("Field for storing '" + filter.getKey() + "' is called '" + fieldName + '\'');
            }
        }
    }

    private Pair<List<String>, List<String>> check(Set<Field> structure) {
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        checkFieldCount(structure, errors, warnings);
        for (Map.Entry<String, Predicate<Field>> f : filters.entrySet()) {
            checkField(f, structure, errors, warnings);
        }

        return new Pair<>(errors, warnings);
    }

    @Override
    public TestResult test() {
        Pair<List<String>, List<String>> errorsAndWarnings = check(Set.of(clazz.getDeclaredFields()));
        StringBuilder notesBuilder = new StringBuilder();
        if (!errorsAndWarnings.first().isEmpty()) {
            notesBuilder.append("\nErrors found in internal structure:");
            for (String error : errorsAndWarnings.first()) {
                notesBuilder.append("\n\t- ")
                    .append(error);
            }
        }
        if (!errorsAndWarnings.second().isEmpty()) {
            notesBuilder.append("\nWarnings for internal structure:");
            for (String warning : errorsAndWarnings.second()) {
                notesBuilder.append("\n\t- ")
                    .append(warning);
            }
        }

        return TestResult.builder()
            .result(errorsAndWarnings.first().isEmpty())
            .notes(notesBuilder.toString())
            .testedObject(clazz)
            .build();
    }
}
