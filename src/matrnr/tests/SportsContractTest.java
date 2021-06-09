package matrnr.tests;

import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Methods;
import matrnr.utils.Misc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public class SportsContractTest extends Test {
    public SportsContractTest() {
        super(
            Set.of(Sports.class),
            "SportsContractTest",
            "Checks whether the methods of Sports fulfill the basic contracts given in the specification",
            Methods.getMethods(Sports.class,
                "getFeeFactor",
                "getFee"
            ),
            Set.of()
        );
    }

    private static boolean getFeeFactorConformsContract() {
        return Misc.bdEq(BigDecimal.ONE, Sports.ARCHERY.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.BASKETBALL.getFeeFactor())
            && Misc.bdEq(new BigDecimal("1.2"), Sports.CLIMBING.getFeeFactor())
            && Misc.bdEq(new BigDecimal("1.8"), Sports.DIVING.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.FOOTBALL.getFeeFactor())
            && Misc.bdEq(new BigDecimal("2.1"), Sports.GOLF.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.HANDBALL.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.HOCKEY.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.MOUNTAINBIKING.getFeeFactor())
            && Misc.bdEq(BigDecimal.ONE, Sports.PARKOUR.getFeeFactor());
    }

    private static boolean getFeeConformsContract() {
        BigDecimal random1 = BigDecimal.valueOf(Math.random() * 10.0);
        BigDecimal random2 = BigDecimal.valueOf(Math.random() * 10.0);
        BigDecimal random3 = BigDecimal.valueOf(Math.random() * 10.0);

        for (Sports s : Sports.values()) {
            if (
                !Misc.bdEq(random1.multiply(s.getFeeFactor()), s.getFee(random1))
                    || !Misc.bdEq(random2.multiply(s.getFeeFactor()), s.getFee(random2))
                    || !Misc.bdEq(random3.multiply(s.getFeeFactor()), s.getFee(random3))
            ) {
                return false;
            }
        }
        return true;
    }

    @Override
    public TestResult test() {
        TestResult.Builder result = TestResult.builder().success();

        try {
            if (!getFeeFactorConformsContract()) {
                result.failure()
                    .appendNote("\nMethod 'getFeeFactor' returns a value that deviates from the specification");
            }

            if (!getFeeConformsContract()) {
                result.failure()
                    .appendNote("\nMethod 'getFee' calculates a wrong result");
            }
        } catch (Throwable t) {
            result.failure()
                .unexpected(t)
                .appendNote("\nThrew unexpected exception");
        }

        result.testedObjects(List.of(Sports.values()));

        return result.build();
    }
}
