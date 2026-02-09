package turip.auth.controller.dto.response;

public record TuripLoginResponse(String nickname) {

    public static TuripLoginResponse from(String nickname) {
        return new TuripLoginResponse(nickname);
    }
}
