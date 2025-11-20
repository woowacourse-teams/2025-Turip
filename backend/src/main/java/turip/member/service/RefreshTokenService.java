package turip.member.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.member.domain.Member;
import turip.member.domain.RefreshToken;
import turip.member.repository.RefreshTokenRepository;

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
