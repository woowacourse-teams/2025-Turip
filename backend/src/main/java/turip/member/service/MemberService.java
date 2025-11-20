package turip.member.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.member.domain.Account;
import turip.member.domain.Member;
import turip.member.domain.Provider;
import turip.member.repository.AccountRepository;
import turip.member.repository.MemberRepository;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final AccountRepository accountRepository;

    public boolean isFirstLogin(Provider provider, String providerId) {
        return !memberRepository.existsByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public Member findOrCreate(Provider provider, String providerId, String email) {
        return memberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Account savedAccount = accountRepository.save(new Account());
                    Member member = new Member(savedAccount, provider, providerId, email);
                    return memberRepository.save(member);
                });
    }
}
