package turip.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.member.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
}
