package turip.util.fixture;

import turip.account.domain.Member;
import turip.account.domain.TuripMember;

public class TuripMemberFixture {

    public static TuripMember createTuripMember() {
        Member member = MemberFixture.createMember();
        return new TuripMember(member, "turip", "1234abcd!");
    }

    public static TuripMember createCustomTuripMember(Member member, String loginId, String loginPassword) {
        return new TuripMember(member, loginId, loginPassword);
    }
}
