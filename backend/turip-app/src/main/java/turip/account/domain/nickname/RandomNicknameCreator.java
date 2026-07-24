package turip.account.domain.nickname;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class RandomNicknameCreator implements NicknameCreator {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public String create() {
        return NicknameWord.createRandomNickname(RANDOM);
    }
}
