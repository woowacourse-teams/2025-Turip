package turip.account.service;

import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.NicknameWord;
import turip.account.domain.Role;
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

    private static final Random RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;
    private final FavoriteContentRepository favoriteContentRepository;
    private final FavoriteFolderService favoriteFolderService;
    private final FavoriteFolderRepository favoriteFolderRepository;

    @Transactional
    public Account create() {
        String nickname = generateUniqueNickname();
        Account account = new Account(Role.USER, nickname);
        Account savedAccount = accountRepository.save(account);
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
        favoriteFolderRepository.deletePersonalFoldersByAccount(account);
        accountRepository.delete(account);
    }

    private String generateUniqueNickname() {
        int maxAttempts = 5;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            String nickname = NicknameWord.createRandomNickname(RANDOM);
            if (!accountRepository.existsByNickname(nickname)) {
                return nickname;
            }
        }

        throw new InternalServerException(ErrorTag.NICKNAME_CREATION_ERROR);
    }
}
