package turip.auth.controller.dto.response;

import turip.auth.service.dto.SocialLoginResult;

public record SocialLoginResponseV1(
        String accessToken,
        String refreshToken,
        String nickname,
        boolean isNewMember
) {
    public static SocialLoginResponseV1 of(SocialLoginResult result) {
        return new SocialLoginResponseV1(
                result.accessToken(),
                result.refreshToken(),
                result.member().getAccount().getNickname(),
                result.isNewMember()
        );
    }
}
