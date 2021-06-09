package matrnr;

import matrnr.suites.Suites;

import java.nio.file.Path;

public class TestMain {
    public static void main(String[] args) {
        TestEngine engine = TestEngine.builder()
            .registerSuite(Suites.STRUCTURE_CHECKS)
            .registerSuite(Suites.NULL_PARAMETER_CHECKS)
            .registerSuite(Suites.NULL_RETURN_CHECKS)
            .registerSuite(Suites.CONTRACT_CHECKS)
            .registerSuite(Suites.SHALLOW_COPY_CHECKS)
            .fullOutput(Path.of("C:\\Users\\rapha\\Desktop\\testresults.txt"))
            .build();

        engine.test();
    }
}
