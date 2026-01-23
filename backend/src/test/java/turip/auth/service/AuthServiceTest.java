package turip.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Date;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.account.domain.SocialMember;
import turip.account.domain.TuripMember;
import turip.account.service.MemberService;
import turip.account.service.SocialMemberService;
import turip.account.service.TuripMemberService;
import turip.auth.controller.dto.request.GoogleLoginRequest;
import turip.auth.controller.dto.request.RefreshTokenRequest;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.RefreshTokenResponse;
import turip.auth.controller.dto.response.SocialLoginResponse;
import turip.auth.domain.RefreshToken;
import turip.auth.service.dto.TuripLoginResult;
import turip.auth.token.GoogleTokenParser;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.UnauthorizedException;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;
import turip.util.fixture.SocialMemberFixture;
import turip.util.fixture.TuripMemberFixture;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private GoogleTokenParser googleTokenParser;

    @Mock
    private MemberService memberService;

    @Mock
    private SocialMemberService socialMemberService;

    @Mock
    private TuripMemberService turipMemberService;

    @Mock
    private RefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthService authService;

    private String testSecretKey = "turip-test-jwt-secret-key-for-testing-purposes-only-32bytes";
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        signingKey = Keys.hmacShaKeyFor(testSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    @DisplayName("loginWithTurip 메서드 테스트")
    @Nested
    class LoginWithTurip {

        @DisplayName("자체 로그인 성공 시 access token과 refresh token을 반환한다")
        @Test
        void loginWithTurip1() {
            // given
            String deviceFid = "device-123";
            String email = "test@gmail.com";
            String loginId = "turip";
            String loginPassword = "ValidPass1!";
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, email, false);
            TuripMember turipMember = TuripMemberFixture.createCustomTuripMember(member, loginId, loginPassword);
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            TuripLoginRequest request = new TuripLoginRequest(loginId, loginPassword);

            when(turipMemberService.login(request)).thenReturn(turipMember);
            when(jwtProvider.generateAccessToken(1L, account.getRole())).thenReturn(accessToken);
            when(jwtProvider.generateRefreshToken(1L, account.getRole())).thenReturn(refreshToken);
            when(jwtProvider.getIssuedAt(anyString())).thenReturn(LocalDateTime.now());
            when(jwtProvider.getExpiration(anyString())).thenReturn(LocalDateTime.now().plusDays(7));
            when(jwtProvider.hashToken(anyString())).thenReturn("hashed-token");

            // when
            TuripLoginResult reslt = authService.loginWithTurip(request, deviceFid);

            // then
            assertThat(reslt).isNotNull();
            assertThat(reslt.accessToken()).isEqualTo(accessToken);
            assertThat(reslt.refreshToken()).isEqualTo(refreshToken);
            assertThat(reslt.role()).isEqualTo(account.getRole());
            verify(refreshTokenService).save(
                    any(Member.class),
                    anyString(),
                    anyString(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }

        @DisplayName("신규 회원인 경우 isNewMember를 true로 반환한다")
        @Test
        void loginWithTurip2() {
            // given
            String deviceFid = "device-123";
            String email = "test@gmail.com";
            String loginId = "turip";
            String loginPassword = "ValidPass1!";
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, email, true);
            TuripMember turipMember = TuripMemberFixture.createCustomTuripMember(member, loginId, loginPassword);
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            TuripLoginRequest request = new TuripLoginRequest(loginId, loginPassword);

            when(turipMemberService.login(request)).thenReturn(turipMember);
            when(jwtProvider.generateAccessToken(1L, account.getRole())).thenReturn(accessToken);
            when(jwtProvider.generateRefreshToken(1L, account.getRole())).thenReturn(refreshToken);
            when(jwtProvider.getIssuedAt(anyString())).thenReturn(LocalDateTime.now());
            when(jwtProvider.getExpiration(anyString())).thenReturn(LocalDateTime.now().plusDays(7));
            when(jwtProvider.hashToken(anyString())).thenReturn("hashed-token");

            // when
            TuripLoginResult reslt = authService.loginWithTurip(request, deviceFid);

            // then
            assertThat(reslt).isNotNull();
            assertThat(reslt.accessToken()).isEqualTo(accessToken);
            assertThat(reslt.refreshToken()).isEqualTo(refreshToken);
            assertThat(reslt.role()).isEqualTo(account.getRole());
            verify(refreshTokenService).save(
                    any(Member.class),
                    anyString(),
                    anyString(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }
    }

    @Nested
    @DisplayName("loginWithSocial 메서드 테스트")
    class LoginWithSocialTest {

        @Test
        @DisplayName("구글 로그인 성공 시 access token과 refresh token을 반환한다")
        void loginWithGoogleSuccess() {
            // given
            String idToken = "valid-google-id-token";
            String deviceFid = "device-123";
            String email = "test@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-123";
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, email, false);
            SocialMember socialMember = SocialMemberFixture.createCustomSocialMember(member, provider, providerId);
            String accessToken = "access-token";
            String refreshToken = "refresh-token";

            GoogleLoginRequest request = new GoogleLoginRequest(idToken);

            when(googleTokenParser.getProvider()).thenReturn(provider);
            when(googleTokenParser.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParser.getEmail(idToken)).thenReturn(email);
            when(socialMemberService.findOrCreate(provider, providerId, email)).thenReturn(socialMember);
            when(jwtProvider.generateAccessToken(1L, account.getRole())).thenReturn(accessToken);
            when(jwtProvider.generateRefreshToken(1L, account.getRole())).thenReturn(refreshToken);
            when(jwtProvider.getIssuedAt(anyString())).thenReturn(LocalDateTime.now());
            when(jwtProvider.getExpiration(anyString())).thenReturn(LocalDateTime.now().plusDays(7));
            when(jwtProvider.hashToken(anyString())).thenReturn("hashed-token");

            // when
            SocialLoginResponse response = authService.loginWithSocial(request, provider, deviceFid);

            // then
            assertThat(member.isFirstLogin()).isFalse();
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("access-token");
            assertThat(response.refreshToken()).isEqualTo("refresh-token");
            assertThat(response.isNewMember()).isFalse();
            verify(refreshTokenService).save(
                    any(Member.class),
                    anyString(),
                    anyString(),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }

        @Test
        @DisplayName("신규 회원인 경우 isNewMember를 true로 반환한다")
        void loginWithNewMember() {
            // given
            String idToken = "valid-google-id-token";
            String deviceFid = "device-123";
            String providerId = "google-user-new";
            String email = "newuser@gmail.com";
            Account account = AccountFixture.createUser();
            Provider provider = Provider.GOOGLE;
            Member member = MemberFixture.createCustomMember(account, email, true);
            SocialMember socialMember = SocialMemberFixture.createCustomSocialMember(member, provider, providerId);

            GoogleLoginRequest request = new GoogleLoginRequest(idToken);

            when(googleTokenParser.getProvider()).thenReturn(provider);
            when(googleTokenParser.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParser.getEmail(idToken)).thenReturn(email);
            when(socialMemberService.findOrCreate(provider, providerId, email)).thenReturn(socialMember);
            when(jwtProvider.generateAccessToken(1L, account.getRole())).thenReturn("access-token");
            when(jwtProvider.generateRefreshToken(1L, account.getRole())).thenReturn("refresh-token");
            when(jwtProvider.getIssuedAt(anyString())).thenReturn(LocalDateTime.now());
            when(jwtProvider.getExpiration(anyString())).thenReturn(LocalDateTime.now().plusDays(7));
            when(jwtProvider.hashToken(anyString())).thenReturn("hashed-token");

            // when
            SocialLoginResponse response = authService.loginWithSocial(request, provider, deviceFid);

            // then
            assertThat(response.isNewMember()).isTrue();
            verify(refreshTokenService).save(
                    eq(member),
                    eq(deviceFid),
                    eq("hashed-token"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }

        @Test
        @DisplayName("유효하지 않은 ID 토큰인 경우 UnauthorizedException이 발생한다")
        void loginWithInvalidIdToken() {
            // given
            String invalidIdToken = "invalid-token";
            String deviceFid = "device-123";
            GoogleLoginRequest request = new GoogleLoginRequest(invalidIdToken);

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(invalidIdToken))
                    .thenThrow(new UnauthorizedException(ErrorTag.ID_TOKEN_NOT_VALID));

            // when & then
            assertThatThrownBy(() -> authService.loginWithSocial(request, Provider.GOOGLE, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.ID_TOKEN_NOT_VALID.getMessage());
        }
    }

    @Nested
    @DisplayName("refresh 메서드 테스트")
    class RefreshTest {

        @Test
        @DisplayName("유효한 refresh token으로 새로운 토큰을 발급받는다")
        void refreshTokenSuccess() {
            // given
            Long accountId = 1L;
            String deviceFid = "device-123";
            String oldRefreshToken = generateValidRefreshToken(accountId);
            String hashedToken = "hashed-old-token";

            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@gmail.com", true);
            RefreshToken storedRefreshToken = new RefreshToken(
                    member, deviceFid, hashedToken,
                    LocalDateTime.now(), LocalDateTime.now().plusDays(7)
            );

            RefreshTokenRequest request = new RefreshTokenRequest(oldRefreshToken);

            Claims claims = Jwts.claims().add("accountId", accountId).build();
            when(jwtProvider.parseToken(oldRefreshToken)).thenReturn(claims);
            when(memberService.getByAccountId(accountId)).thenReturn(member);
            when(refreshTokenService.getByMemberAndDeviceFid(member, deviceFid)).thenReturn(storedRefreshToken);
            when(jwtProvider.hashToken(oldRefreshToken)).thenReturn(hashedToken);
            when(jwtProvider.generateAccessToken(accountId, account.getRole())).thenReturn("new-access-token");
            when(jwtProvider.generateRefreshToken(accountId, account.getRole())).thenReturn("new-refresh-token");
            when(jwtProvider.getIssuedAt(anyString())).thenReturn(LocalDateTime.now());
            when(jwtProvider.getExpiration(anyString())).thenReturn(LocalDateTime.now().plusDays(7));
            when(jwtProvider.hashToken("new-refresh-token")).thenReturn("new-hashed-token");

            // when
            RefreshTokenResponse response = authService.refresh(request, deviceFid);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accessToken()).isEqualTo("new-access-token");
            assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
            verify(refreshTokenService).save(
                    eq(member),
                    eq(deviceFid),
                    eq("new-hashed-token"),
                    any(LocalDateTime.class),
                    any(LocalDateTime.class)
            );
        }

        @Test
        @DisplayName("저장된 토큰과 일치하지 않으면 UnauthorizedException이 발생한다")
        void refreshTokenMismatch() {
            // given
            Long accountId = 1L;
            String deviceFid = "device-123";
            String oldRefreshToken = generateValidRefreshToken(accountId);
            String wrongHashedToken = "wrong-hashed-token";

            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@gmail.com", true);
            RefreshToken storedRefreshToken = new RefreshToken(
                    member, deviceFid, "correct-hashed-token",
                    LocalDateTime.now(), LocalDateTime.now().plusDays(7)
            );

            RefreshTokenRequest request = new RefreshTokenRequest(oldRefreshToken);

            Claims claims = Jwts.claims().add("accountId", accountId).build();
            when(jwtProvider.parseToken(oldRefreshToken)).thenReturn(claims);
            when(memberService.getByAccountId(accountId)).thenReturn(member);
            when(refreshTokenService.getByMemberAndDeviceFid(member, deviceFid)).thenReturn(storedRefreshToken);
            when(jwtProvider.hashToken(oldRefreshToken)).thenReturn(wrongHashedToken);

            // when & then
            assertThatThrownBy(() -> authService.refresh(request, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.REFRESH_TOKEN_INVALID.getMessage());
        }

        @Test
        @DisplayName("DB에 저장된 refresh token이 없으면 UnauthorizedException이 발생한다")
        void refreshTokenNotFound() {
            // given
            Long accountId = 1L;
            String deviceFid = "device-123";
            String oldRefreshToken = generateValidRefreshToken(accountId);

            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@gmail.com", true);

            RefreshTokenRequest request = new RefreshTokenRequest(oldRefreshToken);

            Claims claims = Jwts.claims().add("accountId", accountId).build();
            when(jwtProvider.parseToken(oldRefreshToken)).thenReturn(claims);
            when(memberService.getByAccountId(accountId)).thenReturn(member);
            when(refreshTokenService.getByMemberAndDeviceFid(member, deviceFid)).thenThrow(
                    new UnauthorizedException(ErrorTag.REFRESH_TOKEN_NOT_FOUND));

            // when & then
            assertThatThrownBy(() -> authService.refresh(request, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.REFRESH_TOKEN_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("만료된 refresh token인 경우 UnauthorizedException이 발생한다")
        void refreshTokenExpired() {
            // given
            String expiredToken = "expired-token";
            String deviceFid = "device-123";
            RefreshTokenRequest request = new RefreshTokenRequest(expiredToken);

            when(jwtProvider.parseToken(expiredToken))
                    .thenThrow(new ExpiredJwtException(null, null, "Token expired"));

            // when & then
            assertThatThrownBy(() -> authService.refresh(request, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.REFRESH_TOKEN_EXPIRED.getMessage());
        }

        @Test
        @DisplayName("위조된 refresh token인 경우 UnauthorizedException이 발생한다")
        void refreshTokenInvalidSignature() {
            // given
            String forgedToken = "forged-token";
            String deviceFid = "device-123";
            RefreshTokenRequest request = new RefreshTokenRequest(forgedToken);

            when(jwtProvider.parseToken(forgedToken))
                    .thenThrow(new SignatureException("Invalid signature"));

            // when & then
            assertThatThrownBy(() -> authService.refresh(request, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.REFRESH_TOKEN_SIGNATURE_INVALID.getMessage());
        }

        private String generateValidRefreshToken(Long accountId) {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + 604800000); // 7일

            return Jwts.builder()
                    .header()
                    .type("JWT")
                    .and()
                    .issuedAt(now)
                    .expiration(expiry)
                    .claim("accountId", accountId)
                    .signWith(signingKey)
                    .compact();
        }
    }

    @Nested
    @DisplayName("logout 메서드 테스트")
    class LogoutTest {

        @Test
        @DisplayName("정상적으로 로그아웃이 성공한다")
        void logoutSuccess() {
            // given
            String deviceFid = "device-123";
            Account account = AccountFixture.createUser();
            Member member = MemberFixture.createCustomMember(account, "test@gmail.com", true);

            when(memberService.getByAccountId(account.getId())).thenReturn(member);

            // when
            authService.logout(member.getAccount(), deviceFid);

            // then
            verify(refreshTokenService).deleteByMemberAndDeviceFid(member, deviceFid);
        }

        @Test
        @DisplayName("존재하지 않는 계정으로 로그아웃 시도 시 UnauthorizedException이 발생한다")
        void logoutWithNonExistentAccount() {
            // given
            Long accountId = 999L;
            String deviceFid = "device-123";
            Account account = AccountFixture.createUser();

            when(memberService.getByAccountId(accountId))
                    .thenThrow(new UnauthorizedException(ErrorTag.UNAUTHORIZED));

            // when & then
            assertThatThrownBy(() -> authService.logout(account, deviceFid))
                    .isInstanceOf(UnauthorizedException.class)
                    .hasMessage(ErrorTag.UNAUTHORIZED.getMessage());
        }
    }
}
