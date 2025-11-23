package turip.auth.service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.auth.domain.RefreshToken;
import turip.auth.repository.RefreshTokenRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;
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
        refreshTokenRepository.deleteByMemberAndDeviceFid(member, deviceFid);
        refreshTokenRepository.flush();
        refreshTokenRepository.save(new RefreshToken(member, deviceFid, hashedRefreshToken, issuedAt, expiration));
    }

    public RefreshToken getByMemberAndDeviceFid(Member member, String deviceFid) {
        return refreshTokenRepository.findByMemberAndDeviceFid(member, deviceFid)
                .orElseThrow(() -> new UnauthorizedException(ErrorTag.REFRESH_TOKEN_NOT_FOUND));
    }

    @Transactional
    public void deleteByMemberAndDeviceFid(Member member, String deviceFid) {
        refreshTokenRepository.deleteByMemberAndDeviceFid(member, deviceFid);
    }

    @Transactional
    public void deleteByMember(Member member) {
        refreshTokenRepository.deleteAllByMember(member);
    }
}
