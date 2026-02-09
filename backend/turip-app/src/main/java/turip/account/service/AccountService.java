package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.InternalServerException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final AccountCreateService accountCreateService;

    public Account create() {
        int maxAttempts = 5;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            try {
                return accountCreateService.save();
            } catch (DataIntegrityViolationException ignored) {
            }
        }
        throw new InternalServerException(ErrorTag.ACCOUNT_CREATION_ERROR);
    }

    public Account getById(Long accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException(ErrorTag.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public void deleteAccountAndFavorites(Account account) {
        favoriteContentRepository.deleteByAccount(account);
        favoriteFolderRepository.deletePersonalFoldersByAccount(account);
        accountRepository.delete(account);
    }
}
