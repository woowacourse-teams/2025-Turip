package turip.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.member.domain.Member;
import turip.member.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByMemberAndDeviceFid(Member member, String deviceFid);

    void deleteByMemberAndDeviceFid(Member member, String deviceFid);

    void deleteByMemberIdAndDeviceFid(Long memberId, String deviceFid);
}
