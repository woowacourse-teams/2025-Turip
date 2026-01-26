package turip.util.fixture;

import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;

public class SocialMemberFixture {

    public static SocialMember createSocialMember() {
        Member member = MemberFixture.createMember();
        return new SocialMember(member, Provider.GOOGLE, "google-provider-id");
    }

    public static SocialMember createCustomSocialMember(Member member, Provider provider, String providerId) {
        return new SocialMember(member, provider, providerId);
    }
}
