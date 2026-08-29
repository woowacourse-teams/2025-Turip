package turip.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.repository.ContentRepository;
import turip.controller.dto.response.AdminBookmarkStatusResponse;
import turip.favorite.repository.FavoriteContentRepository;

@Service
@RequiredArgsConstructor
public class AdminBookmarkService {

    private final AccountRepository accountRepository;
    private final ContentRepository contentRepository;
    private final FavoriteContentRepository favoriteContentRepository;

    public List<AdminBookmarkStatusResponse> findAdminAccountBookmarkStatuses(Long contentId) {
        validateContentExists(contentId);

        List<Account> adminAccounts = accountRepository.findAllByRole(Role.ADMIN);
        List<Long> adminAccountIds = adminAccounts.stream()
                .map(Account::getId)
                .toList();

        // 어드민 계정 중 해당 콘텐츠를 북마크한 계정의 id 조회
        Set<Long> bookmarkedAccountIds = favoriteContentRepository.findByAccountIdInAndContentId(adminAccountIds,
                        contentId).stream()
                .map(favoriteContent -> favoriteContent.getAccount().getId())
                .collect(Collectors.toSet());

        return adminAccounts.stream()
                .map(account -> AdminBookmarkStatusResponse.of(account, bookmarkedAccountIds.contains(account.getId())))
                .toList();
    }

    private void validateContentExists(Long contentId) {
        contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
    }
}
