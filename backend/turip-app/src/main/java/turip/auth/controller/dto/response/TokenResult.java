package turip.auth.controller.dto.response;

public record TokenResult(String accessToken, String refreshToken) {

    public static TokenResult of(String accessToken, String refreshToken) {
        return new TokenResult(accessToken, refreshToken);
    }
}
