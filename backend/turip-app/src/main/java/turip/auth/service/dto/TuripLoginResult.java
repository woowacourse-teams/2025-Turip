package turip.auth.service.dto;

public record TuripLoginResult(TokenResult tokenResult, String nickname) {

    public static TuripLoginResult of(TokenResult tokenResult, String nickname) {
        return new TuripLoginResult(tokenResult, nickname);
    }
}
