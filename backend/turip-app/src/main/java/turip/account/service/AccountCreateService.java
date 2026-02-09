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
import turip.favorite.service.FavoriteFolderService;

@Service
@RequiredArgsConstructor
public class AccountCreateService {

    private static final Random RANDOM = new SecureRandom();

    private final FavoriteFolderService favoriteFolderService;
    private final AccountRepository accountRepository;

    @Transactional
    public Account save() {
        String nickname = NicknameWord.createRandomNickname(RANDOM);
        Account account = new Account(Role.USER, nickname);
        Account savedAccount = accountRepository.save(account);
        favoriteFolderService.createDefaultFavoriteFolder(savedAccount);
        return savedAccount;
    }
}
