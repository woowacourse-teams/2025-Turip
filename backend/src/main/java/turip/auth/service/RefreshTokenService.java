package turip.auth.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.auth.domain.RefreshToken;
import turip.auth.repository.RefreshTokenRepository;
import turip.member.domain.Member;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void save(
            Member member,
            String deviceFid,
            String hashedRefreshToken,
            LocalDateTime issuedAt,
            LocalDateTime expiration
    ) {
        refreshTokenRepository.deleteByMemberIdAndDeviceFid(member.getId(), deviceFid);
        refreshTokenRepository.flush();
        refreshTokenRepository.save(new RefreshToken(member, deviceFid, hashedRefreshToken, issuedAt, expiration));
    }
}
