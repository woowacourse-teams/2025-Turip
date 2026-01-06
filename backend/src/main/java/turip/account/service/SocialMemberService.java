package turip.account.service;

import jakarta.transaction.Transactional;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;
import turip.account.repository.SocialMemberRepository;

@Service
@RequiredArgsConstructor
public class SocialMemberService {

    private final SocialMemberRepository socialMemberRepository;

    @Transactional
    public void create(Member member, Provider provider, String providerId) {
        socialMemberRepository.save(new SocialMember(member, provider, providerId));
    }

    public Optional<SocialMember> findByProviderAndProviderId(Provider provider, String providerId) {
        return socialMemberRepository.findByProviderAndProviderId(provider, providerId);
    }

    public boolean existsByProviderAndProviderId(Provider provider, String providerId) {
        return socialMemberRepository.existsByProviderAndProviderId(provider, providerId);
    }

    @Transactional
    public void deleteByMember(Member member) {
        socialMemberRepository.deleteByMember(member);
    }
}
