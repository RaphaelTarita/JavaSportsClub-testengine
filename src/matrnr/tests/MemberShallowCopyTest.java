package matrnr.tests;

import matrnr.Level;
import matrnr.Member;
import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Methods;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MemberShallowCopyTest extends Test {
    private static Constructor<?> getTwoArgConstructor() {
        try {
            return Member.class.getDeclaredConstructor(String.class, Map.class);
        } catch (NoSuchMethodException ex) {
            throw new AssertionError("Two-arg constructor (String, Map) is missing from the Member class", ex);
        }
    }

    public MemberShallowCopyTest() {
        super(
            Set.of(Member.class),
            "MemberShallowCopyTest",
            "Tests whether shallow copies are performed in the Member class wherever this is required per the specification",
            Methods.getMethods(Member.class, "getSports"),
            Set.of(getTwoArgConstructor())
        );
    }

    private static boolean constructorShallowCopy(TestResult.Builder result) {
        Map<Sports, Level> testMap = new HashMap<>();
        testMap.put(Sports.ARCHERY, Level.BEGINNER);
        testMap.put(Sports.BASKETBALL, Level.NORMAL);
        testMap.put(Sports.CLIMBING, Level.ADVANCED);
        testMap.put(Sports.DIVING, Level.PROFESSIONAL);

        Member m = new Member("testName1", testMap);

        result.testedObject(m);

        testMap.put(Sports.FOOTBALL, Level.BEGINNER);
        if (testMap.equals(m.getSports())) {
            return false;
        }

        testMap.remove(Sports.FOOTBALL);
        if (!testMap.equals(m.getSports())) {
            return false;
        }

        testMap.remove(Sports.CLIMBING);
        if (testMap.equals(m.getSports())) {
            return false;
        }

        testMap.put(Sports.CLIMBING, Level.ADVANCED);
        if (!testMap.equals(m.getSports())) {
            return false;
        }

        testMap.put(Sports.ARCHERY, Level.ADVANCED);
        if (testMap.equals(m.getSports())) {
            return false;
        }

        testMap.put(Sports.ARCHERY, Level.BEGINNER);
        return testMap.equals(m.getSports());
    }

    private static boolean getSportsShallowCopy(TestResult.Builder result) {
        Map<Sports, Level> testMap = new HashMap<>();
        testMap.put(Sports.FOOTBALL, Level.BEGINNER);
        testMap.put(Sports.GOLF, Level.NORMAL);
        testMap.put(Sports.HANDBALL, Level.ADVANCED);
        testMap.put(Sports.HOCKEY, Level.PROFESSIONAL);

        Member m = new Member("testName2", testMap);

        result.testedObject(m);

        Map<Sports, Level> shallowCopy = m.getSports();
        shallowCopy.put(Sports.MOUNTAINBIKING, Level.BEGINNER);
        if (m.getSports().containsKey(Sports.MOUNTAINBIKING)) {
            return false;
        }

        shallowCopy.remove(Sports.HOCKEY);
        if (!m.getSports().containsKey(Sports.HOCKEY)) {
            return false;
        }

        shallowCopy.put(Sports.GOLF, Level.ADVANCED);
        if (!m.getSports().get(Sports.GOLF).equals(Level.NORMAL)) {
            return false;
        }

        shallowCopy.remove(Sports.MOUNTAINBIKING);
        shallowCopy.put(Sports.HOCKEY, Level.PROFESSIONAL);
        shallowCopy.put(Sports.GOLF, Level.NORMAL);
        return shallowCopy.equals(m.getSports());
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();

        try {
            if (!constructorShallowCopy(result)) {
                result.failure()
                    .appendNote("\nTwo-Arg Constructor does not perform proper shallow copy");
            }

            if (!getSportsShallowCopy(result)) {
                result.failure()
                    .appendNote("\nMethod 'getSports' does not perform proper shallow copy");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        return result.build();
    }
}
