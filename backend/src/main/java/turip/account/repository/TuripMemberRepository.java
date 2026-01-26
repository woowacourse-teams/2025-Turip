package turip.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.TuripMember;

public interface TuripMemberRepository extends JpaRepository<TuripMember, Long> {

    boolean existsByLoginId(String loginId);

    Optional<TuripMember> findByLoginId(String loginId);
}
