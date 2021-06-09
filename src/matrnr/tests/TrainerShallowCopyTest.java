package matrnr.tests;

import matrnr.Level;
import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.Trainer;
import matrnr.utils.Methods;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TrainerShallowCopyTest extends Test {
    private static Constructor<?> getTwoArgConstructor() {
        try {
            return Trainer.class.getDeclaredConstructor(String.class, Map.class);
        } catch (NoSuchMethodException ex) {
            throw new AssertionError("Two-arg constructor (String, Map) is missing from the Trainer class", ex);
        }
    }

    public TrainerShallowCopyTest() {
        super(
            Set.of(Trainer.class),
            "TrainerShallowCopyTest",
            "Tests whether shallow copies are performed in the Trainer class wherever this is required per the specification",
            Methods.getMethods(Trainer.class, "getAccreditations", "getBillableSports"),
            Set.of(getTwoArgConstructor())
        );
    }

    private static boolean constructorShallowCopy(TestResult.Builder result) {
        Map<Sports, Level> testMap = new HashMap<>();
        testMap.put(Sports.MOUNTAINBIKING, Level.BEGINNER);
        testMap.put(Sports.PARKOUR, Level.NORMAL);
        testMap.put(Sports.ARCHERY, Level.ADVANCED);
        testMap.put(Sports.BASKETBALL, Level.PROFESSIONAL);

        Trainer t = new Trainer("testName1", testMap);

        result.testedObject(t);

        testMap.put(Sports.CLIMBING, Level.BEGINNER);
        if (testMap.equals(t.getAccreditations())) {
            return false;
        }

        testMap.remove(Sports.CLIMBING);
        if (!testMap.equals(t.getAccreditations())) {
            return false;
        }

        testMap.remove(Sports.ARCHERY);
        if (testMap.equals(t.getAccreditations())) {
            return false;
        }

        testMap.put(Sports.ARCHERY, Level.ADVANCED);
        if (!testMap.equals(t.getAccreditations())) {
            return false;
        }

        testMap.put(Sports.MOUNTAINBIKING, Level.ADVANCED);
        if (testMap.equals(t.getAccreditations())) {
            return false;
        }

        testMap.put(Sports.MOUNTAINBIKING, Level.BEGINNER);
        return testMap.equals(t.getAccreditations());
    }

    private static boolean getAccreditationsShallowCopy(TestResult.Builder result) {
        Map<Sports, Level> testMap = new HashMap<>();
        testMap.put(Sports.CLIMBING, Level.BEGINNER);
        testMap.put(Sports.DIVING, Level.NORMAL);
        testMap.put(Sports.FOOTBALL, Level.ADVANCED);
        testMap.put(Sports.GOLF, Level.PROFESSIONAL);

        Trainer t = new Trainer("testName2", testMap);

        result.testedObject(t);

        Map<Sports, Level> shallowCopy = t.getAccreditations();
        shallowCopy.put(Sports.HANDBALL, Level.BEGINNER);
        if (t.getAccreditations().containsKey(Sports.HANDBALL)) {
            return false;
        }

        shallowCopy.remove(Sports.GOLF);
        if (!t.getAccreditations().containsKey(Sports.GOLF)) {
            return false;
        }

        shallowCopy.put(Sports.DIVING, Level.ADVANCED);
        if (!t.getAccreditations().get(Sports.DIVING).equals(Level.NORMAL)) {
            return false;
        }

        shallowCopy.remove(Sports.HANDBALL);
        shallowCopy.put(Sports.GOLF, Level.PROFESSIONAL);
        shallowCopy.put(Sports.DIVING, Level.NORMAL);
        return shallowCopy.equals(t.getAccreditations());
    }

    private static boolean getBillableSportsShallowCopy(TestResult.Builder result) {
        Map<Sports, Level> accreds = Map.of(
            Sports.HANDBALL, Level.BEGINNER,
            Sports.HOCKEY, Level.NORMAL,
            Sports.MOUNTAINBIKING, Level.ADVANCED,
            Sports.PARKOUR, Level.PROFESSIONAL
        );

        Trainer t = new Trainer("testName3", accreds);

        result.testedObject(t);

        t.learn(Sports.ARCHERY, Level.BEGINNER);
        t.learn(Sports.BASKETBALL, Level.BEGINNER);
        t.learn(Sports.CLIMBING, Level.BEGINNER);
        t.learn(Sports.HOCKEY, Level.ADVANCED);

        t.getBillableSports(); // should not cause sports map or accreditations map to change

        return accreds.equals(t.getAccreditations())
            && Map.of(
            Sports.ARCHERY, Level.BEGINNER,
            Sports.BASKETBALL, Level.BEGINNER,
            Sports.CLIMBING, Level.BEGINNER,
            Sports.HANDBALL, Level.BEGINNER,
            Sports.HOCKEY, Level.ADVANCED,
            Sports.MOUNTAINBIKING, Level.ADVANCED,
            Sports.PARKOUR, Level.PROFESSIONAL
        ).equals(t.getSports());
    }

    @Override

    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();

        try {
            if (!constructorShallowCopy(result)) {
                result.failure()
                    .appendNote("\nTwo-Arg Constructor does not perform proper shallow copy");
            }

            if (!getAccreditationsShallowCopy(result)) {
                result.failure()
                    .appendNote("\nMethod 'getAccreditations' does not perform proper shallow copy");
            }

            if (!getBillableSportsShallowCopy(result)) {
                result.failure()
                    .appendNote("\nMethod 'getBillableSports' does not perform proper shallow copies internally");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        return result.build();
    }
}
