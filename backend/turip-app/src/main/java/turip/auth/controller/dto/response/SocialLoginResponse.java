package turip.auth.controller.dto.response;


import turip.auth.service.dto.TokenResult;

public record SocialLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewMember,
        String nickname
) {

    public static SocialLoginResponse of(TokenResult tokenResult, boolean isNewMember, String nickname) {
        return new SocialLoginResponse(tokenResult.accessToken(), tokenResult.refreshToken(), isNewMember, nickname);
    }
}
