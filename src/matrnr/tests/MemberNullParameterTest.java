package matrnr.tests;

import matrnr.Member;

import java.util.Set;

public class MemberNullParameterTest extends NullParameterTest {
    public MemberNullParameterTest() {
        super(Member.class, Set.of(IllegalArgumentException.class), Set.of());
    }
}
