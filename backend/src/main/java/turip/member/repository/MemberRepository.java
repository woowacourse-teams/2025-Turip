package turip.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.member.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByDeviceFid(String deviceFid);
}
