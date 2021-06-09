package matrnr.tests;

import matrnr.Trainer;

import java.util.Set;

public class TrainerNullReturnTest extends NullReturnTest {
    public TrainerNullReturnTest() {
        super(Trainer.class, Set.of());
    }
}
