package turip.auth.service.dto;

import turip.account.domain.Role;

public record TuripLoginResult(
        String accessToken,
        String refreshToken,
        Role role
) {

    public static TuripLoginResult of(TokenResult tokenResult, Role role) {
        return new TuripLoginResult(tokenResult.accessToken(), tokenResult.refreshToken(), role);
    }
}
