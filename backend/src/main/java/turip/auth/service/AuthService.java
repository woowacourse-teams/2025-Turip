package turip.auth.service;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.auth.GoogleTokenParser;
import turip.auth.JwtProvider;
import turip.auth.controller.dto.request.LoginRequest;
import turip.auth.controller.dto.response.LoginResponse;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;
import turip.member.domain.Member;
import turip.member.domain.Provider;
import turip.member.domain.RefreshToken;
import turip.member.repository.RefreshTokenRepository;
import turip.member.service.MemberService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GoogleTokenParser googleTokenParser;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public LoginResponse login(LoginRequest request, Provider provider, String deviceFid) {
        if (provider.equals(Provider.GOOGLE)) {
            return loginWithGoogle(request, deviceFid);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    private LoginResponse loginWithGoogle(LoginRequest request, String deviceFid) {
        String idToken = request.idToken();
        Provider provider = googleTokenParser.getProvider();
        String providerId = googleTokenParser.getProviderId(idToken);
        String email = googleTokenParser.getEmail(idToken);

        boolean isNewMember = memberService.isFirstLogin(provider, providerId);

        Member member = saveMember(provider, providerId, email);
        String accessToken = jwtProvider.generateAccessToken(member.getAccount().getId());
        String refreshToken = jwtProvider.generateRefreshToken(member.getAccount().getId());

        saveRefreshToken(member, refreshToken, deviceFid);

        return LoginResponse.of(accessToken, refreshToken, isNewMember);
    }

    private Member saveMember(Provider provider, String providerId, String email) {
        return memberService.findOrCreate(provider, providerId, email);
    }

    private void saveRefreshToken(Member member, String refreshToken, String deviceFid) {
        try {
            LocalDateTime issuedAt = jwtProvider.getIssuedAt(refreshToken);
            LocalDateTime expiration = jwtProvider.getExpiration(refreshToken);
            refreshTokenRepository.save(
                    new RefreshToken(member, deviceFid, jwtProvider.hashToken(refreshToken), issuedAt, expiration));

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_SIGNATURE_NOT_VALID);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }
}
