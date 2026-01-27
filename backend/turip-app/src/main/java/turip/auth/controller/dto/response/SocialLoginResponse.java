package turip.auth.controller.dto.response;


public record SocialLoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewMember
) {

    public static SocialLoginResponse of(TokenResult tokenResult, boolean isNewMember) {
        return new SocialLoginResponse(tokenResult.accessToken(), tokenResult.refreshToken(), isNewMember);
    }
}
