package turip.auth.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.auth.domain.RefreshToken;
import turip.member.domain.Member;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberAndDeviceFid(Member member, String deviceFid);

    void deleteByMemberIdAndDeviceFid(Long memberId, String deviceFid);
}
