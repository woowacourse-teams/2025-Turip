package turip.auth.controller.dto.response;


import turip.account.domain.Member;
import turip.auth.service.dto.TokenResult;

public record SocialLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewMember,
        String nickname
) {

    public static SocialLoginResponse of(TokenResult tokenResult, boolean isNewMember, Member member) {
        return new SocialLoginResponse(tokenResult.accessToken(), tokenResult.refreshToken(), isNewMember,
                member.getAccount().getNickname());
    }
}
