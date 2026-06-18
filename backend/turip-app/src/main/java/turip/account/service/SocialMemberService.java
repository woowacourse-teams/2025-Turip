package turip.account.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;
import turip.account.repository.SocialMemberRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;

@Service
@RequiredArgsConstructor
public class SocialMemberService {

    private final MemberService memberService;
    private final SocialMemberRepository socialMemberRepository;

    @Transactional
    public SocialMember findOrCreate(Provider provider, String providerId, Optional<String> email) {
        return socialMemberRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    // 신규 회원 생성 시에는 email이 필수
                    String emailValue = email.orElseThrow(
                            () -> new BadRequestException(ErrorTag.ID_TOKEN_NOT_VALID)
                    );
                    Member member = memberService.create(emailValue);
                    return socialMemberRepository.save(new SocialMember(member, provider, providerId));
                });
    }
}
