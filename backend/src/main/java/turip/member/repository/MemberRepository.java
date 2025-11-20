package turip.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import turip.member.domain.Member;
import turip.member.domain.Provider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);
}
