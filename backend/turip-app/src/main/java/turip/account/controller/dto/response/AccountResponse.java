package turip.account.controller.dto.response;

import turip.account.domain.Account;
import turip.account.domain.Role;

public record AccountResponse(Long id, String nickname, Role role) {

    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getNickname(), account.getRole());
    }
}
