package turip.account.service;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Component;
import turip.account.domain.NicknameWord;

@Component
public class RandomNicknameCreator implements NicknameCreator {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public String create() {
        return NicknameWord.createRandomNickname(RANDOM);
    }
}
