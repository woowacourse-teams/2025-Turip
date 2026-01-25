package turip.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;

public interface SocialMemberRepository extends JpaRepository<SocialMember, Long> {

    Optional<SocialMember> findByProviderAndProviderId(Provider provider, String providerId);

    boolean existsByProviderAndProviderId(Provider provider, String providerId);
}
