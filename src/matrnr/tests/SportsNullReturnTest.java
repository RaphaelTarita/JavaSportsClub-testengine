package matrnr.tests;

import matrnr.Sports;

import java.util.Set;

public class SportsNullReturnTest extends NullReturnTest {
    public SportsNullReturnTest() {
        super(Sports.class, Set.of());
    }
}
