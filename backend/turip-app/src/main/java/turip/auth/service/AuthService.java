package turip.auth.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.account.service.MemberService;
import turip.account.service.SocialMemberService;
import turip.account.service.TuripMemberService;
import turip.auth.controller.dto.request.RefreshTokenRequest;
import turip.auth.controller.dto.request.SocialLoginRequest;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.RefreshTokenResponse;
import turip.auth.domain.RefreshToken;
import turip.auth.service.dto.SocialLoginResult;
import turip.auth.service.dto.TokenResult;
import turip.auth.service.dto.TuripLoginResult;
import turip.auth.token.AppleTokenParser;
import turip.auth.token.GoogleTokenParser;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.UnauthorizedException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final GoogleTokenParser googleTokenParser;
    private final AppleTokenParser appleTokenParser;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;
    private final SocialMemberService socialMemberService;
    private final TuripMemberService turipMemberService;

    @Transactional
    public TuripLoginResult loginWithTurip(TuripLoginRequest request, String deviceFid) {
        Member member = loginAndGetMember(request);
        TokenResult tokenResult = processTuripLogin(deviceFid, member);
        return TuripLoginResult.of(tokenResult, member);
    }

    @Transactional
    public Member loginAndGetMember(TuripLoginRequest request) {
        return turipMemberService.login(request).getMember();
    }

    @Transactional
    public TokenResult processTuripLogin(final String deviceFid, final Member member) {
        if (member.isFirstLogin()) {
            member.completeFirstLogin();
        }
        return issueToken(deviceFid, member);
    }

    @Transactional
    public SocialLoginResult loginWithSocial(SocialLoginRequest request, Provider provider, String deviceFid) {
        if (provider == Provider.GOOGLE) {
            return loginWithGoogle(request, deviceFid);
        }
        if (provider == Provider.APPLE) {
            return loginWithApple(request, deviceFid);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    @Transactional
    public RefreshTokenResponse refresh(RefreshTokenRequest request, String deviceFid) {
        String refreshToken = request.refreshToken();

        try {
            Long accountId = jwtProvider.getClaimOfName(refreshToken, "accountId", Long.class);
            Member member = getMemberByAccountId(accountId);
            RefreshToken storedRefreshToken = refreshTokenService.getByMemberAndDeviceFid(member, deviceFid);

            verifyRefreshTokenMatch(refreshToken, storedRefreshToken);
            validateRefreshTokenExpiration(storedRefreshToken);

            String newAccessToken = jwtProvider.generateAccessToken(accountId, member.getAccount().getRole());
            String newRefreshToken = jwtProvider.generateRefreshToken(accountId, member.getAccount().getRole());

            saveRefreshToken(member, newRefreshToken, deviceFid);

            return RefreshTokenResponse.of(newAccessToken, newRefreshToken);

        } catch (UnauthorizedException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag(), e);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
        }
    }

    @Transactional
    public void logout(Account account, String deviceFid) {
        Member member = getMemberByAccountId(account.getId());
        refreshTokenService.deleteByMemberAndDeviceFid(member, deviceFid);
    }

    private TokenResult issueToken(String deviceFid, Member member) {
        Long accountId = member.getAccount().getId();
        Role role = member.getAccount().getRole();

        String accessToken = jwtProvider.generateAccessToken(accountId, role);
        String refreshToken = jwtProvider.generateRefreshToken(accountId, role);

        saveRefreshToken(member, refreshToken, deviceFid);
        return TokenResult.of(accessToken, refreshToken);
    }

    private SocialLoginResult loginWithGoogle(SocialLoginRequest request, String deviceFid) {
        String idToken = request.idToken();
        validateIdToken(idToken);

        Provider provider = googleTokenParser.getProvider();
        String providerId = googleTokenParser.getProviderId(idToken);
        String email = googleTokenParser.getEmail(idToken);

        Member member = findOrCreateSocialMember(provider, providerId, email);

        boolean isNewMember = false;
        if (member.isFirstLogin()) {
            member.completeFirstLogin();
            isNewMember = true;
        }
        TokenResult tokenResult = issueToken(deviceFid, member);

        return SocialLoginResult.of(tokenResult, isNewMember, member);
    }

    private Member findOrCreateSocialMember(Provider provider, String providerId, String email) {
        return socialMemberService.findOrCreate(provider, providerId, email).getMember();
    }

    private Member getMemberByAccountId(Long accountId) {
        try {
            return memberService.getByAccountId(accountId);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
        }
    }

    private void saveRefreshToken(Member member, String refreshToken, String deviceFid) {
        try {
            LocalDateTime issuedAt = jwtProvider.getIssuedAt(refreshToken);
            LocalDateTime expiration = jwtProvider.getExpiration(refreshToken);
            refreshTokenService.save(member, deviceFid, jwtProvider.hashToken(refreshToken), issuedAt, expiration);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag(), e);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
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

    private SocialLoginResult loginWithApple(SocialLoginRequest request, String deviceFid) {
        String idToken = request.idToken();
        validateIdToken(idToken);

        Provider provider = appleTokenParser.getProvider();
        String providerId = appleTokenParser.getProviderId(idToken);
        String email = appleTokenParser.getEmail(idToken);

        Member member = findOrCreateSocialMember(provider, providerId, email);

        boolean isNewMember = false;
        if (member.isFirstLogin()) {
            member.completeFirstLogin();
            isNewMember = true;
        }
        TokenResult tokenResult = issueToken(deviceFid, member);

        return SocialLoginResult.of(tokenResult, isNewMember, member);
    }
}
