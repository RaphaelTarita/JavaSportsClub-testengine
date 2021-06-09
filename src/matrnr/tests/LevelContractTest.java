package matrnr.tests;

import matrnr.Level;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Methods;

import java.util.List;
import java.util.Set;

public class LevelContractTest extends Test {
    public LevelContractTest() {
        super(
            Set.of(Level.class),
            "LevelContractTest",
            "Checks whether the methods of Level fulfill the basic contracts given in the specification",
            Methods.getMethodsInTree(Level.class, "getMappedName", "next", "toString"),
            Set.of()
        );
    }

    private static boolean nextConformsContract() {
        return Level.BEGINNER.next().equals(Level.NORMAL)
            && Level.NORMAL.next().equals(Level.ADVANCED)
            && Level.ADVANCED.next().equals(Level.PROFESSIONAL)
            && Level.PROFESSIONAL.next().equals(Level.PROFESSIONAL);
    }

    private static boolean toStringReturnsMappedName() {
        for (Level l : Level.values()) {
            if (!l.getMappedName().equals(l.toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();

        try {
            if (!nextConformsContract()) {
                result.failure()
                    .appendNote("\nThe 'next' method does not work according to the specification");
            }

            if (!toStringReturnsMappedName()) {
                result.failure()
                    .appendNote("\n'toString' does not return the mapped name (or the getter is not right)");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        result.testedObjects(List.of(Level.values()));

        return result.build();
    }
}
