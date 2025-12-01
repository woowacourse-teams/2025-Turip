package turip.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.auth.controller.dto.request.LoginRequest;
import turip.auth.controller.dto.request.RefreshTokenRequest;
import turip.auth.controller.dto.response.LoginResponse;
import turip.auth.controller.dto.response.RefreshTokenResponse;
import turip.auth.domain.RefreshToken;
import turip.auth.token.GoogleTokenParser;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.UnauthorizedException;
import turip.member.domain.Account;
import turip.member.domain.Member;
import turip.member.domain.Provider;
import turip.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GoogleTokenParser googleTokenParser;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    @Transactional
    public LoginResponse login(LoginRequest request, Provider provider, String deviceFid) {
        if (provider == Provider.GOOGLE) {
            return loginWithGoogle(request, deviceFid);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request, String deviceFid) {
        String refreshToken = request.refreshToken();

        try {
            Long accountId = jwtProvider.parseToken(refreshToken).get("accountId", Long.class);
            Member member = getMemberByAccountId(accountId);
            RefreshToken storedRefreshToken = refreshTokenService.getByMemberAndDeviceFid(member, deviceFid);

            verifyRefreshTokenMatch(refreshToken, storedRefreshToken);
            validateRefreshTokenExpiration(storedRefreshToken);

            String newAccessToken = jwtProvider.generateAccessToken(accountId);
            String newRefreshToken = jwtProvider.generateRefreshToken(accountId);

            saveRefreshToken(member, newRefreshToken, deviceFid);

            return new RefreshTokenResponse(newAccessToken, newRefreshToken);

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_SIGNATURE_INVALID);
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }

    @Transactional
    public void logout(Account account, String deviceFid) {
        Member member = getMemberByAccountId(account.getId());
        refreshTokenService.deleteByMemberAndDeviceFid(member, deviceFid);
    }

    private LoginResponse loginWithGoogle(LoginRequest request, String deviceFid) {
        String idToken = request.idToken();
        validateIdToken(idToken);

        Provider provider = googleTokenParser.getProvider();
        String providerId = googleTokenParser.getProviderId(idToken);
        String email = googleTokenParser.getEmail(idToken);

        boolean isNewMember = memberService.isFirstLogin(provider, providerId);

        Member member = findOrCreateMember(provider, providerId, email);
        String accessToken = jwtProvider.generateAccessToken(member.getAccount().getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getAccount().getId());

        saveRefreshToken(member, refreshToken, deviceFid);

        return LoginResponse.of(accessToken, refreshToken, isNewMember);
    }

    private Member findOrCreateMember(Provider provider, String providerId, String email) {
        return memberService.findOrCreate(provider, providerId, email);
    }

    private Member getMemberByAccountId(Long accountId) {
        try {
            return memberService.getByAccountId(accountId);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }

    private void saveRefreshToken(Member member, String refreshToken, String deviceFid) {
        try {
            LocalDateTime issuedAt = jwtProvider.getIssuedAt(refreshToken);
            LocalDateTime expiration = jwtProvider.getExpiration(refreshToken);
            refreshTokenService.save(member, deviceFid, jwtProvider.hashToken(refreshToken), issuedAt, expiration);
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }

    private void verifyRefreshTokenMatch(String oldRefreshToken, RefreshToken storedRefreshToken) {
        String oldTokenHash = jwtProvider.hashToken(oldRefreshToken);
        if (!storedRefreshToken.getTokenHash().equals(oldTokenHash)) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_INVALID);
        }
    }

    private void validateRefreshTokenExpiration(RefreshToken storedRefreshToken) {
        if (storedRefreshToken.isExpired()) {
            throw new UnauthorizedException(ErrorTag.REFRESH_TOKEN_EXPIRED);
        }
    }

    private void validateIdToken(String idToken) {
        if (idToken == null || idToken.isBlank()) {
            throw new BadRequestException(ErrorTag.ID_TOKEN_NOT_VALID);
        }
    }
}
