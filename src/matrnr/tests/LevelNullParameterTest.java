package matrnr.tests;

import matrnr.Level;

import java.util.Set;

public class LevelNullParameterTest extends NullParameterTest {
    public LevelNullParameterTest() {
        super(Level.class, Set.of(IllegalArgumentException.class), Set.of("valueOf"));
    }
}
