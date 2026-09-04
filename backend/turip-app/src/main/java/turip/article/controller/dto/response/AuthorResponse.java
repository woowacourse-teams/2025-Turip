package turip.article.controller.dto.response;

import turip.account.domain.Account;

public record AuthorResponse(
        Long id,
        String nickname
) {

    public static AuthorResponse from(Account account) {
        if (account == null) {
            return null;
        }
        return new AuthorResponse(account.getId(), account.getNickname());
    }
}
