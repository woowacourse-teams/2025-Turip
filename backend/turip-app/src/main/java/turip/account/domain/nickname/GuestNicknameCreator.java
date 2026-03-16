package turip.account.domain.nickname;

import java.security.SecureRandom;
import java.util.Random;
import org.springframework.stereotype.Component;

@Component
public class GuestNicknameCreator implements NicknameCreator {

    private static final Random RANDOM = new SecureRandom();

    @Override
    public String create() {
        int number = RANDOM.nextInt(1_000_000);
        return "게스트_" + number;
    }
}
