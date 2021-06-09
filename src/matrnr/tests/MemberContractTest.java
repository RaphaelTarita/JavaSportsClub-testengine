package matrnr.tests;

import matrnr.Level;
import matrnr.Member;
import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Methods;
import matrnr.utils.Misc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MemberContractTest extends Test {
    public MemberContractTest() {
        super(
            Set.of(Member.class),
            "MemberContractTest",
            "Checks whether the methods of Member fulfill the basic contracts given in the specification",
            Methods.getMethods(Member.class,
                "getName",
                "getSports",
                "getBillableSports",
                "learn",
                "toString",
                "compareTo",
                "equals",
                "hashCode"
            ),
            Set.of(Member.class.getDeclaredConstructors())
        );
    }

    private static boolean firstConstructorThrowsExceptions(TestResult.Builder result) {
        try {
            new Member(null);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            new Member("");
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }
        return true;
    }

    private static boolean secondConstructorDelegatesToFirst(TestResult.Builder result) {
        try {
            new Member(null, Map.of(Sports.ARCHERY, Level.BEGINNER));
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            new Member("", Map.of(Sports.ARCHERY, Level.BEGINNER));
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }
        return true;
    }

    private static boolean secondConstructorThrowsExceptions(TestResult.Builder result) {
        try {
            new Member("testName", null);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            new Member("testName", new HashMap<>());
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            Map<Sports, Level> map = new HashMap<>();
            map.put(Sports.ARCHERY, null);
            new Member("testName", map);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }
        return true;
    }

    private static boolean getNameReturnsName(TestResult.Builder result) {
        String name = Misc.getRandomString();
        Member m = new Member(name);
        result.testedObject(m);
        return name.equals(m.getName());
    }

    private static boolean getSportsReturnsSports(TestResult.Builder result) {
        Map<Sports, Level> sports = Map.of(
            Sports.ARCHERY, Level.BEGINNER,
            Sports.BASKETBALL, Level.NORMAL,
            Sports.CLIMBING, Level.ADVANCED,
            Sports.DIVING, Level.PROFESSIONAL,
            Sports.FOOTBALL, Level.BEGINNER,
            Sports.GOLF, Level.NORMAL,
            Sports.HANDBALL, Level.ADVANCED,
            Sports.HOCKEY, Level.PROFESSIONAL,
            Sports.MOUNTAINBIKING, Level.BEGINNER,
            Sports.PARKOUR, Level.NORMAL
        );
        Member m = new Member("testName1", sports);
        result.testedObject(m);
        return sports.equals(m.getSports());
    }

    private static boolean getBillableSportsReturnsSportsKeySet(TestResult.Builder result) {
        Map<Sports, Level> sports = Map.of(
            Sports.ARCHERY, Level.BEGINNER,
            Sports.BASKETBALL, Level.BEGINNER,
            Sports.DIVING, Level.BEGINNER,
            Sports.FOOTBALL, Level.BEGINNER,
            Sports.HANDBALL, Level.BEGINNER,
            Sports.HOCKEY, Level.BEGINNER,
            Sports.PARKOUR, Level.BEGINNER
        );
        Member m = new Member("testName2", sports);
        result.testedObject(m);
        return sports.keySet().equals(m.getBillableSports());
    }

    private static boolean learnConformsToContract(TestResult.Builder result) {
        Member m = new Member("testName3", Map.of(
            Sports.ARCHERY, Level.BEGINNER,
            Sports.CLIMBING, Level.ADVANCED,
            Sports.DIVING, Level.PROFESSIONAL,
            Sports.GOLF, Level.NORMAL,
            Sports.HOCKEY, Level.PROFESSIONAL,
            Sports.MOUNTAINBIKING, Level.BEGINNER
        ));
        result.testedObject(m);

        return m.learn(Sports.BASKETBALL, Level.BEGINNER).equals(Level.BEGINNER)
            && m.learn(Sports.FOOTBALL, Level.ADVANCED).equals(Level.BEGINNER)
            && m.learn(Sports.ARCHERY, Level.NORMAL).equals(Level.NORMAL)
            && m.learn(Sports.GOLF, Level.PROFESSIONAL).equals(Level.ADVANCED)
            && m.learn(Sports.CLIMBING, Level.ADVANCED).equals(Level.ADVANCED)
            && m.learn(Sports.HOCKEY, Level.BEGINNER).equals(Level.PROFESSIONAL);
    }

    private static boolean toStringFormatsCorrectly(TestResult.Builder result) {
        String name = Misc.getRandomString();
        Map<Sports, Level> sports = Map.of(
            Sports.BASKETBALL, Level.NORMAL,
            Sports.FOOTBALL, Level.BEGINNER,
            Sports.HANDBALL, Level.ADVANCED,
            Sports.MOUNTAINBIKING, Level.BEGINNER
        );
        Member m = new Member(name, sports);
        result.testedObject(m);

        Pattern expected = Pattern.compile("name:\\s*"
            + name
            + ",\\s*sports:\\s*"
            + "(.*)"
            + "\\s*");
        Matcher mt = expected.matcher(m.toString());

        if (!mt.matches()) {
            return false;
        }

        for (Map.Entry<Sports, Level> entry : sports.entrySet()) {
            if (!mt.group(1).contains(entry.toString())) {
                return false;
            }
        }
        return true;
    }

    private static boolean compareToComparesCorrectly(TestResult.Builder result) {
        String name1 = Misc.getRandomString();
        String name2 = Misc.getRandomString();
        Member m1 = new Member(name1, Map.of(Sports.ARCHERY, Level.BEGINNER));
        Member m2 = new Member(name2, Map.of(Sports.BASKETBALL, Level.PROFESSIONAL));

        result.testedObject(m1)
            .testedObject(m2);

        if (m1.compareTo(m1) != 0 || m2.compareTo(m2) != 0) {
            return false;
        }

        if (
            m1.compareTo(new Member(name1, Map.of(Sports.PARKOUR, Level.PROFESSIONAL))) != 0
                || m2.compareTo(new Member(name2, Map.of(Sports.MOUNTAINBIKING, Level.ADVANCED))) != 0
        ) {
            return false;
        }

        int comparison1 = m1.compareTo(m2);
        int comparison2 = m2.compareTo(m1);

        if (comparison1 < 0 && comparison2 > 0) {
            return name1.compareTo(name2) < 0 && name2.compareTo(name1) > 0;
        } else if (comparison1 > 0 && comparison2 < 0) {
            return name1.compareTo(name2) > 0 && name2.compareTo(name1) < 0;
        } else if (comparison1 == 0 && comparison2 == 0) {
            return name1.compareTo(name2) == 0 && name2.compareTo(name1) == 0;
        } else {
            return false;
        }
    }

    private static boolean equalsComparesCorrectly(TestResult.Builder result) {
        String name1 = Misc.getRandomString();
        String name2 = Misc.getRandomString();
        Member m1 = new Member(name1, Map.of(Sports.CLIMBING, Level.NORMAL));
        Member m2 = new Member(name2, Map.of(Sports.HOCKEY, Level.NORMAL));

        result.testedObject(m1)
            .testedObject(m2);

        if (!m1.equals(m1) || !m2.equals(m2)) {
            return false;
        }

        if (
            !m1.equals(new Member(name1, Map.of(Sports.DIVING, Level.ADVANCED)))
                || !m2.equals(new Member(name2, Map.of(Sports.HANDBALL, Level.BEGINNER)))
        ) {
            return false;
        }

        if (m1.equals(m2) != m2.equals(m1)) {
            return false;
        }

        return m1.equals(m2) == name1.equals(name2);
    }

    private static boolean hashCodeHashesCorrectly(TestResult.Builder result) {
        String name1 = Misc.getRandomString();
        String name2 = Misc.getRandomString();
        Member m1 = new Member(name1, Map.of(Sports.FOOTBALL, Level.PROFESSIONAL));
        Member m2 = new Member(name2, Map.of(Sports.GOLF, Level.PROFESSIONAL));

        result.testedObject(m1)
            .testedObject(m2);

        if (
            m1.hashCode() != new Member(name1, Map.of(Sports.GOLF, Level.BEGINNER)).hashCode()
                || m2.hashCode() != new Member(name2, Map.of(Sports.FOOTBALL, Level.ADVANCED)).hashCode()
        ) {
            return false;
        }

        return (m1.hashCode() == m2.hashCode()) == (name1.hashCode() == name2.hashCode());
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();
        try {
            if (!firstConstructorThrowsExceptions(result)) {
                result.failure()
                    .appendNote("\nOne-arg constructor does not throw exceptions on null or empty string inputs");
            } else if (!secondConstructorDelegatesToFirst(result)) {
                result.failure()
                    .appendNote("\nTwo-arg constructor does not delegate to one-arg constructor");
            }

            if (!secondConstructorThrowsExceptions(result)) {
                result.failure()
                    .appendNote("\nTwo-arg constructor does not throw exceptions on null, empty or nullvalue inputs");
            }

            if (!getNameReturnsName(result)) {
                result.failure()
                    .appendNote("\nMethod 'getName' does not return the name of the member");
            }

            if (!getSportsReturnsSports(result)) {
                result.failure()
                    .appendNote("\nMethod 'getSports' does not return 'sports' from member");
            }

            if (!getBillableSportsReturnsSportsKeySet(result)) {
                result.failure()
                    .appendNote("\nMethod 'getBillableSports' does not return the set of all sports");
            }

            if (!learnConformsToContract(result)) {
                result.failure()
                    .appendNote("\nMethod 'learn' does not work according to the specification");
            }

            if (!toStringFormatsCorrectly(result)) {
                result.failure()
                    .appendNote("\n'toString' returns a string that does not conform to the format specification");
            }

            if (!compareToComparesCorrectly(result)) {
                result.failure()
                    .appendNote("\n'compareTo' comparisons are invalid, or do not (just) take 'name' into account");
            }

            if (!equalsComparesCorrectly(result)) {
                result.failure()
                    .appendNote("\n'equals' comparisons are invalid, or do not (just) take 'name' into account");
            }

            if (!hashCodeHashesCorrectly(result)) {
                result.failure()
                    .appendNote("\n'hashCode' hashings are invalid, or do not (just) take 'name' into account");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        return result.build();
    }
}
