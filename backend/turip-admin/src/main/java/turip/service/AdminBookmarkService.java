package turip.service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.controller.dto.response.AdminBookmarkStatusResponse;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;

@Service
@RequiredArgsConstructor
public class AdminBookmarkService {

    private static final int ONE_WEEK = 1;

    private final AccountRepository accountRepository;
    private final ContentRepository contentRepository;
    private final FavoriteContentRepository favoriteContentRepository;

    public List<AdminBookmarkStatusResponse> findAdminAccountBookmarkStatuses(Long contentId) {
        validateContentExists(contentId);

        List<Account> adminAccounts = accountRepository.findAllByRole(Role.ADMIN);
        List<Long> adminAccountIds = adminAccounts.stream()
                .map(Account::getId)
                .toList();

        LocalDate lastWeekMonday = getLastWeekMonday();
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(6);

        // 어드민 계정 중 지난주 기간에 해당 콘텐츠를 북마크한 계정의 id 조회
        Set<Long> bookmarkedAccountIds = favoriteContentRepository.findByAccountIdInAndContentIdAndCreatedAtBetween(
                        adminAccountIds, contentId, lastWeekMonday, lastWeekSunday).stream()
                .map(favoriteContent -> favoriteContent.getAccount().getId())
                .collect(Collectors.toSet());

        return adminAccounts.stream()
                .map(account -> AdminBookmarkStatusResponse.of(account, bookmarkedAccountIds.contains(account.getId())))
                .toList();
    }

    @Transactional
    public void upsertBookmark(Long accountId, Long contentId) {
        Account admin = getAdmin(accountId);
        Content content = getContent(contentId);

        Optional<FavoriteContent> existing = favoriteContentRepository.findByAccountIdAndContentId(accountId,
                contentId);
        if (existing.isPresent()) {
            existing.get().updateCreatedAt(getLastWeekMonday());
            return;
        }

        favoriteContentRepository.save(new FavoriteContent(getLastWeekMonday(), admin, content));
    }

    @Transactional
    public void deleteBookmark(Long accountId, Long contentId) {
        favoriteContentRepository.findByAccountIdAndContentId(accountId, contentId)
                .ifPresent(favoriteContentRepository::delete);
    }

    private Account getAdmin(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ADMIN_NOT_FOUND));
        if (account.getRole() != Role.ADMIN) {
            throw new NotFoundException(ErrorTag.ADMIN_NOT_FOUND);
        }
        return account;
    }

    private Content getContent(Long contentId) {
        return contentRepository.findById(contentId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.CONTENT_NOT_FOUND));
    }

    private void validateContentExists(Long contentId) {
        getContent(contentId);
    }

    private LocalDate getLastWeekMonday() {
        return LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(ONE_WEEK);
    }
}
