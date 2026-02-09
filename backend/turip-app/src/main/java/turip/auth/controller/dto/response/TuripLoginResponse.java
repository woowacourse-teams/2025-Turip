package turip.auth.controller.dto.response;

import turip.auth.service.dto.TuripLoginResult;

public record TuripLoginResponse(String nickname) {

    public static TuripLoginResponse from(TuripLoginResult turipLoginResult) {
        return new TuripLoginResponse(turipLoginResult.nickname());
    }
}
