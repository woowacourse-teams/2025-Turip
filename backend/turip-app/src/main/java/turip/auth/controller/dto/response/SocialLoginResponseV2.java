package turip.auth.controller.dto.response;

import turip.auth.service.dto.SocialLoginResult;

public record SocialLoginResponseV2(
        String accessToken,
        String refreshToken,
        String nickname,
        boolean isMigrationDecided
) {
    public static SocialLoginResponseV2 of(SocialLoginResult result) {
        return new SocialLoginResponseV2(
                result.accessToken(),
                result.refreshToken(),
                result.nickname(),
                result.isMigrationDecided()
        );
    }
}
