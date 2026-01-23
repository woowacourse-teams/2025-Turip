package turip.auth.controller.dto.response;

import turip.account.domain.Role;
import turip.auth.service.dto.TuripLoginResult;

public record TuripLoginResponse(Role role) {

    public static TuripLoginResponse from(TuripLoginResult turipLoginResult) {
        return new TuripLoginResponse(turipLoginResult.role());
    }
}
