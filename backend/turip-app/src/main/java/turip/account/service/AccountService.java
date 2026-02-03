package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.service.FavoriteFolderAccountService;
import turip.favorite.service.FavoriteFolderService;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderService favoriteFolderService;
    private final FavoriteFolderAccountService favoriteFolderAccountService;

    @Transactional
    public Account create() {
        Account savedAccount = accountRepository.save(Account.createUserAccount());
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
        favoriteFolderAccountService.removeByAccount(account);
        accountRepository.delete(account);
    }
}
