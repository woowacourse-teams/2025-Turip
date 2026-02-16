package turip.auth.controller.dto.response;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        boolean isNewMember
) {

    public static LoginResponse of(String accessToken, String refreshToken, boolean isNewMember) {
        return new LoginResponse(accessToken, refreshToken, isNewMember);
    }
}
