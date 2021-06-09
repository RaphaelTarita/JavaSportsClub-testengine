package matrnr.tests;

import matrnr.Level;
import matrnr.Member;
import matrnr.Sports;
import matrnr.SportsClub;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.Trainer;
import matrnr.utils.Methods;
import matrnr.utils.Misc;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SportsClubContractTest extends Test {
    public SportsClubContractTest() {
        super(
            Set.of(SportsClub.class),
            "SportsClubContractTest",
            "Checks whether the methods of SportsClub fulfill the basic contracts given in the specification",
            Methods.getMethods(
                SportsClub.class,
                "getName",
                "getFeePerSports",
                "getMembers",
                "getSports",
                "calculateMembershipFee",
                "registerSports",
                "addMember",
                "removeMember",
                "toString"
            ),
            Set.of(SportsClub.class.getDeclaredConstructors())
        );
    }

    private static boolean constructorThrowsExceptions(TestResult.Builder result) {
        try {
            new SportsClub(null, BigDecimal.ONE);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            new SportsClub("", BigDecimal.ONE);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        try {
            new SportsClub("testName", null);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }
        return true;
    }

    private static boolean getNameReturnsName(TestResult.Builder result) {
        String name = Misc.getRandomString();
        SportsClub sc = new SportsClub(name, BigDecimal.TEN);

        result.testedObject(sc);

        return name.equals(sc.getName());
    }

    private static boolean getFeePerSportsReturnsFee(TestResult.Builder result) {
        BigDecimal feePerSports = BigDecimal.valueOf(Math.random() * 10.0);
        SportsClub sc = new SportsClub("testName1", feePerSports);

        result.testedObject(sc);

        return Misc.bdEq(feePerSports, sc.getFeePerSports());
    }

    private static boolean getMembersReturnsMembers(TestResult.Builder result) {
        SportsClub sc = new SportsClub("testName2", BigDecimal.TEN);
        Member m1 = new Member("memberTestName1");
        Member m2 = new Member("memberTestName2");
        Member m3 = new Member("memberTestName3");

        sc.addMember(m1);
        sc.addMember(m2);
        sc.addMember(m3);

        result.testedObject(sc);

        return sc.getMembers().equals(Set.of(m1, m2, m3));
    }

    private static boolean getSportsReturnsOfferedSports(TestResult.Builder result) {
        Map<Sports, Level> offeredSports = Misc.getRandomSportsMap();
        Trainer offeredSportsTrainer = new Trainer("trainerTestName1", offeredSports);
        SportsClub sc = new SportsClub("testName3", BigDecimal.TEN);
        sc.addMember(offeredSportsTrainer);

        result.testedObject(sc);

        return offeredSports.keySet().equals(sc.getSports());
    }

    private static boolean calculateMembershipFeeThrowsException(TestResult.Builder result) {
        Member isMember = new Member("InTheSportsclub1");
        Member isNotMember = new Member("NotInTheSportsclub1");
        SportsClub sc = new SportsClub("testName4", BigDecimal.TEN);
        sc.addMember(isMember);

        result.testedObject(sc)
            .testedObject(isNotMember);

        try {
            sc.calculateMembershipFee(isNotMember);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        return true;
    }

    private static boolean calculateMembershipFeeReturnsFee(TestResult.Builder result) {
        BigDecimal feePerSports = BigDecimal.valueOf(Math.random() * 10.0);
        Map<Sports, Level> offeredSports = Misc.getRandomSportsMap();
        Map<Sports, Level> requestedSports = Misc.getRandomSportsMap();
        SportsClub sc = new SportsClub("testName5", feePerSports);
        Trainer offeredSportsTrainer = new Trainer("trainerTestName2", offeredSports);
        Member toTest = new Member("memberTestName4", requestedSports);

        sc.addMember(offeredSportsTrainer);
        sc.addMember(toTest);

        result.testedObject(sc)
            .testedObject(toTest);

        Set<Sports> intersection = offeredSports.keySet();
        intersection.retainAll(requestedSports.keySet());
        BigDecimal expected = intersection.stream().reduce(BigDecimal.ZERO, (res, s) -> res.add(s.getFee(feePerSports)), BigDecimal::add);

        BigDecimal actual = sc.calculateMembershipFee(toTest);

        return Misc.bdEq(expected, actual);
    }

    private static boolean registerSportsThrowsException(TestResult.Builder result) {
        Member isMember = new Member("InTheSportsclub2");
        Member isNotMember = new Member("NotInTheSportsclub2");
        SportsClub sc = new SportsClub("testName6", BigDecimal.TEN);
        sc.addMember(isMember);

        result.testedObject(sc)
            .testedObject(isNotMember);

        try {
            sc.registerSports(isNotMember, Sports.ARCHERY, Level.BEGINNER);
            return false;
        } catch (IllegalArgumentException ex) {
            result.expected(ex);
            // pass
        }

        return true;
    }

    private static boolean registerSportsConformsContract(TestResult.Builder result) {
        Member shouldWork1 = new Member("memberTestName5");
        Member shouldWork2 = new Member("memberTestName6", Map.of(Sports.ARCHERY, Level.NORMAL));
        Member shouldWork3 = new Member("memberTestName7", Map.of(Sports.ARCHERY, Level.PROFESSIONAL));
        Member shouldNotWork1 = new Member("memberTestName8", Map.of(Sports.HOCKEY, Level.BEGINNER));
        Member shouldNotWork2 = new Member("memberTestName9", Map.of(Sports.BASKETBALL, Level.ADVANCED));
        Member shouldNotWork3 = new Member("memberTestName10", Map.of(Sports.ARCHERY, Level.BEGINNER));

        Trainer offeredSportsTrainer = new Trainer("trainerTestName3", Map.of(Sports.ARCHERY, Level.PROFESSIONAL, Sports.BASKETBALL, Level.ADVANCED));

        SportsClub sc = new SportsClub("testName7", BigDecimal.TEN);
        sc.addMember(shouldWork1);
        sc.addMember(shouldWork2);
        sc.addMember(shouldWork3);
        sc.addMember(shouldNotWork1);
        sc.addMember(shouldNotWork2);
        sc.addMember(shouldNotWork3);
        sc.addMember(offeredSportsTrainer);

        result.testedObject(sc);

        return sc.registerSports(shouldWork1, Sports.ARCHERY, Level.BEGINNER)
            && sc.registerSports(shouldWork2, Sports.ARCHERY, Level.ADVANCED)
            && sc.registerSports(shouldWork3, Sports.ARCHERY, Level.PROFESSIONAL)
            && !sc.registerSports(shouldNotWork1, Sports.HOCKEY, Level.NORMAL)
            && !sc.registerSports(shouldNotWork2, Sports.BASKETBALL, Level.PROFESSIONAL)
            && !sc.registerSports(shouldNotWork3, Sports.ARCHERY, Level.ADVANCED);
    }

    private static boolean registerSportsInvokesLearn(TestResult.Builder result) {
        Member shouldLearn1 = new Member("memberTestName11", Map.of(Sports.ARCHERY, Level.BEGINNER));
        Member shouldLearn2 = new Member("memberTestName12");
        Member shouldLearn3 = new Member("memberTestName13", Map.of(Sports.ARCHERY, Level.NORMAL));

        Trainer offeredSportsTrainer = new Trainer("trainerTestName4", Map.of(Sports.ARCHERY, Level.PROFESSIONAL));

        SportsClub sc = new SportsClub("testName8", BigDecimal.TEN);
        sc.addMember(shouldLearn1);
        sc.addMember(shouldLearn2);
        sc.addMember(shouldLearn3);
        sc.addMember(offeredSportsTrainer);

        result.testedObject(sc);

        sc.registerSports(shouldLearn1, Sports.ARCHERY, Level.NORMAL);
        sc.registerSports(shouldLearn2, Sports.ARCHERY, Level.BEGINNER);
        sc.registerSports(shouldLearn3, Sports.ARCHERY, Level.PROFESSIONAL);

        return Level.NORMAL.equals(shouldLearn1.getSports().get(Sports.ARCHERY))
            && Level.BEGINNER.equals(shouldLearn2.getSports().get(Sports.ARCHERY))
            && Level.ADVANCED.equals(shouldLearn3.getSports().get(Sports.ARCHERY));
    }

    private static boolean addMemberConformsContract(TestResult.Builder result) {
        SportsClub sc = new SportsClub("testName9", BigDecimal.TEN);
        Trainer shouldOfferSports = new Trainer("trainerTestName5", Map.of(Sports.HOCKEY, Level.ADVANCED));
        Member shouldNotOfferSports = new Member("memberTestName14", Map.of(Sports.GOLF, Level.PROFESSIONAL));

        Member testMember1 = new Member("memberTestName15", Map.of(Sports.HOCKEY, Level.NORMAL));
        Member testMember2 = new Member("memberTestName16", Map.of(Sports.HOCKEY, Level.ADVANCED));
        Member testMember3 = new Member("memberTestName17", Map.of(Sports.GOLF, Level.BEGINNER));
        if (!sc.addMember(testMember1) || !sc.addMember(testMember2) || !sc.addMember(testMember3)) {
            return false;
        }

        result.testedObject(sc);

        if (!sc.addMember(shouldOfferSports)) {
            result.testedObject(shouldOfferSports);
            return false;
        }

        if (!sc.registerSports(testMember1, Sports.HOCKEY, Level.ADVANCED)) {
            return false;
        }

        if (sc.registerSports(testMember2, Sports.HOCKEY, Level.PROFESSIONAL)) {
            return false;
        }

        if (!sc.addMember(shouldNotOfferSports)) {
            return false;
        }

        if (sc.registerSports(testMember3, Sports.GOLF, Level.NORMAL)) {
            return false;
        }

        if (
            sc.addMember(testMember1)
                || sc.addMember(testMember2)
                || sc.addMember(testMember3)
                || sc.addMember(shouldOfferSports)
                || sc.addMember(shouldNotOfferSports)
        ) {
            return false;
        }

        return shouldOfferSports.getAccreditations().keySet().equals(sc.getSports())
            && Set.of(shouldOfferSports, shouldNotOfferSports, testMember1, testMember2, testMember3).equals(sc.getMembers());
    }

    private static boolean removeMemberConformsContract(TestResult.Builder result) {
        SportsClub sc = new SportsClub("testName10", BigDecimal.TEN);
        Member isMember1 = new Member("memberTestName18");
        Member isMember2 = new Member("memberTestName19", Map.of(Sports.BASKETBALL, Level.BEGINNER));
        Member isMember3 = new Member("memberTestName20");
        Member isMember4 = new Member("memberTestName21", Map.of(Sports.BASKETBALL, Level.ADVANCED));
        Trainer isMember5 = new Trainer("trainerTestName6", Map.of(Sports.BASKETBALL, Level.ADVANCED));
        Trainer isMember6 = new Trainer("trainerTestName7", Map.of(Sports.BASKETBALL, Level.PROFESSIONAL));
        Member isNotMember = new Member("memberTestName22");

        sc.addMember(isMember1);
        sc.addMember(isMember2);
        sc.addMember(isMember3);
        sc.addMember(isMember4);
        sc.addMember(isMember5);
        sc.addMember(isMember6);

        result.testedObject(sc)
            .testedObject(isNotMember);

        // precondition
        if (!Set.of(isMember1, isMember2, isMember3, isMember4, isMember5, isMember6).equals(sc.getMembers())) {
            return false;
        }

        if (!sc.removeMember(isMember1)) {
            return false;
        }

        if (!Set.of(isMember2, isMember3, isMember4, isMember5, isMember6).equals(sc.getMembers())) {
            return false;
        }

        // precondition
        if (!sc.registerSports(isMember2, Sports.BASKETBALL, Level.NORMAL)) {
            return false;
        }

        if (!sc.removeMember(isMember6)) {
            return false;
        }

        if (!Set.of(isMember2, isMember3, isMember4, isMember5).equals(sc.getMembers())) {
            return false;
        }

        if (!sc.registerSports(isMember2, Sports.BASKETBALL, Level.ADVANCED)) {
            return false;
        }

        if (sc.registerSports(isMember4, Sports.BASKETBALL, Level.PROFESSIONAL)) {
            return false;
        }

        if (!sc.removeMember(isMember5)) {
            return false;
        }

        if (!Set.of(isMember2, isMember3, isMember4).equals(sc.getMembers())) {
            return false;
        }

        if (sc.registerSports(isMember3, Sports.BASKETBALL, Level.BEGINNER)) {
            return false;
        }

        return !sc.removeMember(isNotMember);
    }

    private static boolean toStringFormatsCorrectly(TestResult.Builder result) {
        String name = Misc.getRandomString();
        BigDecimal feePerSports = BigDecimal.valueOf(Math.random() * 10.0);
        Map<Sports, Level> offeredSports1 = Misc.getRandomSportsMap(0.15);
        Map<Sports, Level> offeredSports2 = Misc.getRandomSportsMap(0.15);
        Map<Sports, Level> offeredSports3 = Misc.getRandomSportsMap(0.15);
        Map<Sports, Level> offeredSports4 = Misc.getRandomSportsMap(0.15);
        Trainer t1 = new Trainer(Misc.getRandomString(), offeredSports1);
        Trainer t2 = new Trainer(Misc.getRandomString(), offeredSports2);
        Trainer t3 = new Trainer(Misc.getRandomString(), offeredSports3);
        Trainer t4 = new Trainer(Misc.getRandomString(), offeredSports4);

        SportsClub sc = new SportsClub(name, feePerSports);
        sc.addMember(t1);
        sc.addMember(t2);
        sc.addMember(t3);
        sc.addMember(t4);

        result.testedObject(sc);

        Set<Sports> union = new HashSet<>();
        union.addAll(offeredSports1.keySet());
        union.addAll(offeredSports2.keySet());
        union.addAll(offeredSports3.keySet());
        union.addAll(offeredSports4.keySet());

        Map<Sports, Set<Trainer>> expectedOfferedSports = union.stream()
            .collect(Collectors.toMap(Function.identity(), s -> {
                Set<Trainer> trainers = new HashSet<>();
                if (offeredSports1.containsKey(s)) {
                    trainers.add(t1);
                }
                if (offeredSports2.containsKey(s)) {
                    trainers.add(t2);
                }
                if (offeredSports3.containsKey(s)) {
                    trainers.add(t3);
                }
                if (offeredSports4.containsKey(s)) {
                    trainers.add(t4);
                }
                return trainers;
            }));

        Pattern expected = Pattern.compile("SportsClub\\[\\s*name:\\s*"
            + name
            + ",\\s*feePerSports:\\s*"
            + feePerSports
            + ",\\s*offeredSports:\\s*"
            + "(.*)"
            + "\\s*]\\s*");

        Matcher m = expected.matcher(sc.toString());
        return m.matches()
            && m.group(1).contains(t1.toString())
            && m.group(1).contains(t2.toString())
            && m.group(1).contains(t3.toString())
            && m.group(1).contains(t4.toString());

    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();
        try {
            if (!constructorThrowsExceptions(result)) {
                result.failure()
                    .appendNote("\nConstructor did not throw exceptions on invalid input");
            }

            if (!getNameReturnsName(result)) {
                result.failure()
                    .appendNote("\nMethod 'getName' did not return the SportsClub's name");
            }

            if (!getFeePerSportsReturnsFee(result)) {
                result.failure()
                    .appendNote("\nMethod 'getFeePerSports' did not return the right fee");
            }

            if (!getMembersReturnsMembers(result)) {
                result.failure()
                    .appendNote("\nMethod 'getMembers' did not return the member set (or 'addMember' is not implemented correctly)");
            }

            if (!getSportsReturnsOfferedSports(result)) {
                result.failure()
                    .appendNote("\nMethod 'getSports' did not return offered sports (or 'addMember' is not implemented correctly)");
            }

            if (!calculateMembershipFeeThrowsException(result)) {
                result.failure()
                    .appendNote("\nMethod 'calculateMembershipFee' did not throw exception on invalid input");
            }

            if (!calculateMembershipFeeReturnsFee(result)) {
                result.failure()
                    .appendNote("\nMethod 'calculateMembershipFee' returned a wrong result");
            }

            if (!registerSportsThrowsException(result)) {
                result.failure()
                    .appendNote("\nMethod 'registerSports' did not throw exception on invalid input");
            }

            if (!registerSportsConformsContract(result)) {
                result.failure()
                    .appendNote("\nMethod 'registerSports' does not return according to specification");
            }

            if (!registerSportsInvokesLearn(result)) {
                result.failure()
                    .appendNote("\nMethod 'registerSports' does not let member learn the sport");
            }

            if (!addMemberConformsContract(result)) {
                result.failure()
                    .appendNote("\nMethod 'addMember' does not work according to specification");
            }

            if (!removeMemberConformsContract(result)) {
                result.failure()
                    .appendNote("\nMethod 'removeMember' does not work according to specification");
            }

            if (!toStringFormatsCorrectly(result)) {
                result.failure()
                    .appendNote("\n'toString' returns a string that does not conform to the format specification");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nthrew unexpected exception");
        }
        return result.build();
    }
}
