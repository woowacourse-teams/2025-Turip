package turip.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.service.FavoriteFolderService;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderService favoriteFolderService;

    @Transactional
    public Account create() {
        Account savedAccount = accountRepository.save(new Account());
        favoriteFolderService.createDefaultFavoriteFolder(savedAccount);
        return savedAccount;
    }

    public Account getById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public void deleteAccountAndFavorites(Account account) {
        favoriteContentRepository.deleteByAccount(account);
        favoriteFolderService.removeByAccount(account);
        accountRepository.delete(account);
    }
}
