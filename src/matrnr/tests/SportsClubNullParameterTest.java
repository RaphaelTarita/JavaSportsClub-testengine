package matrnr.tests;

import matrnr.SportsClub;

import java.util.Set;

public class SportsClubNullParameterTest extends NullParameterTest {
    public SportsClubNullParameterTest() {
        super(SportsClub.class, Set.of(IllegalArgumentException.class), Set.of());
    }
}
