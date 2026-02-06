package turip.account.service;

import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.NicknameWord;
import turip.account.repository.AccountRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Service
@RequiredArgsConstructor
public class NicknameCreateService {

    private static final Random RANDOM = new SecureRandom();

    private final AccountRepository accountRepository;

    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    @Retryable(retryFor = IllegalArgumentException.class, maxAttempts = 5)
    public String generateUniqueNickname() {
        String nickname = NicknameWord.createRandomNickname(RANDOM);
        if (accountRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException(ErrorTag.NICKNAME_CREATION_ERROR);
        }
        return nickname;
    }
}
