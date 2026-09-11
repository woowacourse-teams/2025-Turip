package turip.controller.dto.response;

import turip.account.domain.Account;

public record AdminBookmarkStatusResponse(
        Long accountId,
        String nickname,
        boolean isBookmarked
) {
    public static AdminBookmarkStatusResponse of(Account account, boolean isBookmarked) {
        return new AdminBookmarkStatusResponse(account.getId(), account.getNickname(), isBookmarked);
    }
}
