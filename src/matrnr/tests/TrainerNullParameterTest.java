package matrnr.tests;

import matrnr.Trainer;

import java.util.Set;

public class TrainerNullParameterTest extends NullParameterTest {
    public TrainerNullParameterTest() {
        super(Trainer.class, Set.of(IllegalArgumentException.class), Set.of());
    }
}
