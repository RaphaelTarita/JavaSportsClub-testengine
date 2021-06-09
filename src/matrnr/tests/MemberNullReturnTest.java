package matrnr.tests;

import matrnr.Member;

import java.util.Set;

public class MemberNullReturnTest extends NullReturnTest {
    public MemberNullReturnTest() {
        super(Member.class, Set.of());
    }
}
