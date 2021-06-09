package matrnr.tests;

import matrnr.Member;
import matrnr.SportsClub;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.Trainer;
import matrnr.utils.Methods;
import matrnr.utils.Misc;

import java.math.BigDecimal;
import java.util.Set;

public class SportsClubShallowCopyTest extends Test {
    public SportsClubShallowCopyTest() {
        super(
            Set.of(SportsClub.class),
            "SportsClubShallowCopyTest",
            "Tests whether shallow copies are performed in the Trainer class wherever this is required per the specification",
            Methods.getMethods(SportsClub.class, "getMembers"),
            Set.of()
        );
    }

    private static boolean getMembersShallowCopy(TestResult.Builder result) {
        Member m1 = new Member("memberTestName1");
        Member m2 = new Member("memberTestName2");
        Member m3 = new Member("memberTestName3");
        Trainer t1 = new Trainer("trainerTestName1", Misc.getRandomSportsMap(0.1));
        Trainer t2 = new Trainer("trainerTestName2", Misc.getRandomSportsMap(0.1));
        Trainer t3 = new Trainer("trainerTestName3", Misc.getRandomSportsMap(0.1));

        SportsClub sc = new SportsClub("testName1", BigDecimal.TEN);
        sc.addMember(m1);
        sc.addMember(m2);
        sc.addMember(m3);
        sc.addMember(t1);
        sc.addMember(t2);
        sc.addMember(t3);

        result.testedObject(sc);

        Set<Member> shallowCopy = sc.getMembers();

        Member shouldNotAppear1 = new Member("memberTestName4");
        Trainer shouldNotAppear2 = new Trainer("trainerTestName4", Misc.getRandomSportsMap());
        shallowCopy.add(shouldNotAppear1);
        shallowCopy.add(shouldNotAppear2);
        if (sc.getMembers().contains(shouldNotAppear1) || sc.getMembers().contains(shouldNotAppear2)) {
            return false;
        }

        shallowCopy.remove(m3);
        shallowCopy.remove(t2);
        if (!sc.getMembers().contains(m3) || !sc.getMembers().contains(t2)) {
            return false;
        }

        shallowCopy.remove(shouldNotAppear1);
        shallowCopy.remove(shouldNotAppear2);
        shallowCopy.add(m3);
        shallowCopy.add(t2);

        return shallowCopy.equals(sc.getMembers());
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();

        try {
            if (!getMembersShallowCopy(result)) {
                result.failure()
                    .appendNote("\nMethod 'getMembers' does not perform proper shallow copy");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        return result.build();
    }
}
