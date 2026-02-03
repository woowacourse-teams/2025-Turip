package turip.account.service;

import java.security.SecureRandom;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.NicknameWord;
import turip.account.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberCreateService {

    private static final Random RANDOM = new SecureRandom();

    private final MemberRepository memberRepository;

    @Retryable(retryFor = DuplicateKeyException.class, maxAttempts = 5)
    public Member save(Account account, String email) {
        String nickname = NicknameWord.createRandomNickname(RANDOM);
        Member member = new Member(account, email, nickname, true);
        return memberRepository.save(member);
    }
}
