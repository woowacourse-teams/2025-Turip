package turip.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import turip.account.domain.Member;
import turip.account.domain.Provider;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByProviderAndProviderId(Provider provider, String providerId);

    Optional<Member> findByProviderAndProviderId(Provider provider, String providerId);

    Optional<Member> findByAccountId(Long accountId);
}
