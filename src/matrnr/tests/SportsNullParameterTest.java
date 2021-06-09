package matrnr.tests;

import matrnr.Sports;

import java.util.Set;

public class SportsNullParameterTest extends NullParameterTest {
    public SportsNullParameterTest() {
        super(Sports.class, Set.of(IllegalArgumentException.class), Set.of("valueOf"));
    }
}
