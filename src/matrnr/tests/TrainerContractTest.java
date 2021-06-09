package matrnr.tests;

import matrnr.Level;
import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.Trainer;
import matrnr.utils.Methods;
import matrnr.utils.Misc;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TrainerContractTest extends Test {
    public TrainerContractTest() {
        super(
            Set.of(Trainer.class),
            "TrainerContractTest",
            "Checks whether the methods of Trainer fulfill the basic contracts given in the specification",
            Methods.getMethods(Trainer.class,
                "getAccreditations",
                "getBillableSports",
                "toString"
            ),
            Set.of(Trainer.class.getDeclaredConstructors())
        );
    }

    private static boolean constructorConformsContract(TestResult.Builder result) {
        String name = Misc.getRandomString();
        Map<Sports, Level> accreds = Misc.getRandomSportsMap();
        Trainer toTest = new Trainer(name, accreds);

        result.testedObject(toTest);

        return name.equals(toTest.getName())
            && accreds.equals(toTest.getSports())
            && accreds.equals(toTest.getAccreditations());
    }

    private static boolean getAccreditationsReturnsAccreditations(TestResult.Builder result) {
        String rName = Misc.getRandomString();
        String tName = "testName1";
        Map<Sports, Level> rAccreds = Misc.getRandomSportsMap();
        Map<Sports, Level> tAccreds = Map.of(
            Sports.DIVING, Level.BEGINNER,
            Sports.HANDBALL, Level.NORMAL,
            Sports.MOUNTAINBIKING, Level.PROFESSIONAL,
            Sports.HOCKEY, Level.ADVANCED,
            Sports.GOLF, Level.ADVANCED
        );
        Trainer rTrainer = new Trainer(rName, rAccreds);
        Trainer tTrainer = new Trainer(tName, tAccreds);

        result.testedObject(rTrainer)
            .testedObject(tTrainer);

        if (!rAccreds.equals(rTrainer.getAccreditations()) || !tAccreds.equals(tTrainer.getAccreditations())) {
            return false;
        }

        tTrainer.learn(Sports.HANDBALL, Level.ADVANCED);
        if (!tAccreds.equals(tTrainer.getAccreditations()) || tTrainer.getAccreditations().equals(tTrainer.getSports())) {
            return false;
        }

        tTrainer.learn(Sports.BASKETBALL, Level.BEGINNER);
        return tAccreds.equals(tTrainer.getAccreditations()) && !tTrainer.getAccreditations().equals(tTrainer.getSports());
    }

    private static boolean getBillableSportsReturnsCorrectSet(TestResult.Builder result) {
        Map<Sports, Level> accreds = Map.of(
            Sports.CLIMBING, Level.BEGINNER,
            Sports.GOLF, Level.NORMAL,
            Sports.DIVING, Level.PROFESSIONAL,
            Sports.FOOTBALL, Level.ADVANCED,
            Sports.ARCHERY, Level.ADVANCED
        );

        Trainer toTest = new Trainer("testName2", accreds);

        result.testedObject(toTest);

        if (!Set.of().equals(toTest.getBillableSports())) {
            return false;
        }

        toTest.learn(Sports.BASKETBALL, Level.BEGINNER);
        toTest.learn(Sports.HOCKEY, Level.BEGINNER);
        toTest.learn(Sports.HANDBALL, Level.BEGINNER);
        toTest.learn(Sports.GOLF, Level.ADVANCED);

        return Set.of(Sports.BASKETBALL, Sports.HOCKEY, Sports.HANDBALL).equals(toTest.getBillableSports());
    }

    private static boolean toStringFormatsCorrectly(TestResult.Builder result) {
        String name = Misc.getRandomString();
        Map<Sports, Level> accreds = Misc.getRandomSportsMap();
        Trainer toTest = new Trainer(name, accreds);

        result.testedObject(toTest);

        Pattern expected = Pattern.compile(
            "name:\\s*"
                + name
                + ",\\s*sports:\\s*"
                + "(.*)"
                + "\\s*,\\s*accreditations:\\s*"
                + "(.*)"
                + "\\s*"
        );
        Matcher m = expected.matcher(toTest.toString());

        if (!m.matches()) {
            return false;
        }

        for (Map.Entry<Sports, Level> entry : accreds.entrySet()) {
            if (!m.group(1).contains(entry.toString()) || !m.group(2).contains(entry.toString())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();
        try {
            if (!constructorConformsContract(result)) {
                result.failure()
                    .appendNote("\nConstructor does not conform to specification");
            }

            if (!getAccreditationsReturnsAccreditations(result)) {
                result.failure()
                    .appendNote("\nMethod 'getAccreditations' does not return the accreditations map (or it is affected by 'learn')");
            }

            if (!getBillableSportsReturnsCorrectSet(result)) {
                result.failure()
                    .appendNote("\nMethod 'getBillableSports' does not return the correct set of sports");
            }

            if (!toStringFormatsCorrectly(result)) {
                result.failure()
                    .appendNote("\n'toString' returns a string that does not conform to the format specification");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        return result.build();
    }
}
