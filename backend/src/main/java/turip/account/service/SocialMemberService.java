package turip.account.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;
import turip.account.repository.SocialMemberRepository;

@Service
@RequiredArgsConstructor
public class SocialMemberService {

    private final MemberService memberService;
    private final SocialMemberRepository socialMemberRepository;

    @Transactional
    public SocialMember findOrCreate(Provider provider, String providerId, String email) {
        return socialMemberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    Member member = memberService.create(email);
                    return socialMemberRepository.save(new SocialMember(member, provider, providerId));
                });
    }
}
