package matrnr.tests;

import matrnr.Sports;
import matrnr.Test;
import matrnr.TestResult;
import matrnr.utils.Methods;

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
        return BigDecimal.ONE.equals(Sports.ARCHERY.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.BASKETBALL.getFeeFactor())
            && new BigDecimal("1.2").equals(Sports.CLIMBING.getFeeFactor())
            && new BigDecimal("1.8").equals(Sports.DIVING.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.FOOTBALL.getFeeFactor())
            && new BigDecimal("2.1").equals(Sports.GOLF.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.HANDBALL.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.HOCKEY.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.MOUNTAINBIKING.getFeeFactor())
            && BigDecimal.ONE.equals(Sports.PARKOUR.getFeeFactor());
    }

    private static boolean getFeeConformsContract() {
        BigDecimal random1 = BigDecimal.valueOf(Math.random() * 10.0);
        BigDecimal random2 = BigDecimal.valueOf(Math.random() * 10.0);
        BigDecimal random3 = BigDecimal.valueOf(Math.random() * 10.0);

        for (Sports s : Sports.values()) {
            if (
                !random1.multiply(s.getFeeFactor()).equals(s.getFee(random1))
                    || !random2.multiply(s.getFeeFactor()).equals(s.getFee(random2))
                    || !random3.multiply(s.getFeeFactor()).equals(s.getFee(random3))
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
