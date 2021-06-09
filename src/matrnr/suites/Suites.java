package matrnr.suites;

import matrnr.TestSuite;
import matrnr.tests.LevelContractTest;
import matrnr.tests.LevelNullParameterTest;
import matrnr.tests.LevelNullReturnTest;
import matrnr.tests.LevelStructureCheck;
import matrnr.tests.MemberContractTest;
import matrnr.tests.MemberNullParameterTest;
import matrnr.tests.MemberNullReturnTest;
import matrnr.tests.MemberShallowCopyTest;
import matrnr.tests.MemberStructureCheck;
import matrnr.tests.SportsClubContractTest;
import matrnr.tests.SportsClubNullParameterTest;
import matrnr.tests.SportsClubNullReturnTest;
import matrnr.tests.SportsClubShallowCopyTest;
import matrnr.tests.SportsClubStructureCheck;
import matrnr.tests.SportsContractTest;
import matrnr.tests.SportsNullParameterTest;
import matrnr.tests.SportsNullReturnTest;
import matrnr.tests.SportsStructureCheck;
import matrnr.tests.TrainerContractTest;
import matrnr.tests.TrainerNullParameterTest;
import matrnr.tests.TrainerNullReturnTest;
import matrnr.tests.TrainerShallowCopyTest;
import matrnr.tests.TrainerStructureCheck;

public class Suites {
    private Suites() {
    }

    public static TestSuite STRUCTURE_CHECKS = new TestSuite(
        "StructureChecks",
        new LevelStructureCheck(),
        new MemberStructureCheck(),
        new SportsClubStructureCheck(),
        new SportsStructureCheck(),
        new TrainerStructureCheck()
    );

    public static TestSuite NULL_PARAMETER_CHECKS = new TestSuite(
        "NullParameterChecks",
        new LevelNullParameterTest(),
        new MemberNullParameterTest(),
        new SportsClubNullParameterTest(),
        new SportsNullParameterTest(),
        new TrainerNullParameterTest()
    );

    public static TestSuite NULL_RETURN_CHECKS = new TestSuite(
        "NullReturnChecks",
        new LevelNullReturnTest(),
        new MemberNullReturnTest(),
        new SportsClubNullReturnTest(),
        new SportsNullReturnTest(),
        new TrainerNullReturnTest()
    );

    public static TestSuite CONTRACT_CHECKS = new TestSuite(
        "ContractChecks",
        new LevelContractTest(),
        new MemberContractTest(),
        new SportsClubContractTest(),
        new SportsContractTest(),
        new TrainerContractTest()
    );

    public static TestSuite SHALLOW_COPY_CHECKS = new TestSuite(
        "ShallowCopyChecks",
        new MemberShallowCopyTest(),
        new SportsClubShallowCopyTest(),
        new TrainerShallowCopyTest()
    );
}
