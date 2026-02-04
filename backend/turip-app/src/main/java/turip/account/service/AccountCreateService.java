package turip.account.service;

import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;
import turip.account.domain.NicknameWord;
import turip.account.domain.Role;
import turip.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountCreateService {

    private static final Random RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;

    @Retryable(retryFor = DuplicateKeyException.class, maxAttempts = 5)
    public Account createUserAccount() {
        String nickname = NicknameWord.createRandomNickname(RANDOM);
        Account account = new Account(Role.USER, nickname);
        return accountRepository.save(account);
    }
}
