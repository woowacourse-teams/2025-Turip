package turip.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.TuripMember;

public interface TuripMemberRepository extends JpaRepository<TuripMember, Long> {

    boolean existsByLoginId(String loginId);
}
