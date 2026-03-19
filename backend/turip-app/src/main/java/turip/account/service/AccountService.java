package turip.account.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.account.domain.nickname.NicknameCreator;
import turip.account.repository.AccountInsertRepository;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.InternalServerException;
import turip.common.exception.custom.NotFoundException;
import turip.favorite.repository.FavoriteContentRepository;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.service.FavoriteFolderService;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FavoriteFolderService favoriteFolderService;
    private final AccountInsertRepository accountInsertRepository;

    @Transactional
    public Account create(NicknameCreator nicknameCreator) {
        int maxAttempts = 5;
        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String nickname = nicknameCreator.create();
            Account account = new Account(Role.USER, nickname);
            Optional<Account> savedAccountResult = accountInsertRepository.trySave(account);
            if (savedAccountResult.isEmpty()) {
                continue;
            }
            Account savedAccount = savedAccountResult.get();
            favoriteFolderService.createDefaultFavoriteFolder(savedAccount);
            return savedAccount;
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
