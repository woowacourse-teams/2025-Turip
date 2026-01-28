package turip.auth.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import javax.crypto.SecretKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.account.service.TuripMemberService;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.MemberFixture;
import turip.util.fixture.TuripMemberFixture;

@ExtendWith(MockitoExtension.class)
class AuthAdminArgumentResolverTest {

    @InjectMocks
    private AuthAdminArgumentResolver authAdminArgumentResolver;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TuripMemberService turipMemberService;

    @Mock
    private MethodParameter methodParameter;

    @Mock
    private NativeWebRequest nativeWebRequest;

    private String testSecretKey = "turip-test-jwt-secret-key-for-testing-purposes-only-32bytes";
    private SecretKey signingKey;

    @BeforeEach
    void setUp() {
        signingKey = Keys.hmacShaKeyFor(testSecretKey.getBytes(StandardCharsets.UTF_8));
    }

    @DisplayName("resolveArgument 메서드 테스트")
    @Nested
    class ResolveArgumentTest {

        @DisplayName("ADMIN role을 가진 유효한 토큰으로 TuripMember를 반환한다")
        @Test
        void resolveArgumentWithAdminRole() {
            // given
            Long accountId = 1L;
            String accessToken = generateValidAccessToken(accountId, Role.ADMIN);
            String bearerToken = "Bearer " + accessToken;

            Account account = AccountFixture.createCustomAccount(accountId, Role.ADMIN);
            Member member = MemberFixture.createCustomMember(account, "admin@test.com", false);
            TuripMember turipMember = TuripMemberFixture.createCustomTuripMember(member, "admin", "AdminPass1!");

            when(nativeWebRequest.getHeader("Authorization")).thenReturn(bearerToken);
            when(jwtProvider.getClaimOfName(accessToken, "accountId", Long.class)).thenReturn(accountId);
            when(jwtProvider.getClaimOfName(accessToken, "role", String.class)).thenReturn(Role.ADMIN.name());
            when(turipMemberService.getByAccountId(accountId)).thenReturn(turipMember);

            // when
            Object result = authAdminArgumentResolver.resolveArgument(methodParameter, null, nativeWebRequest, null);

            // then
            assertThat(result).isEqualTo(turipMember);
        }

        @DisplayName("USER role을 가진 토큰으로 요청하면 ForbiddenException이 발생한다")
        @Test
        void resolveArgumentWithUserRole() {
            // given
            Long accountId = 1L;
            String accessToken = generateValidAccessToken(accountId, Role.USER);
            String bearerToken = "Bearer " + accessToken;

            when(nativeWebRequest.getHeader("Authorization")).thenReturn(bearerToken);
            when(jwtProvider.getClaimOfName(accessToken, "accountId", Long.class)).thenReturn(accountId);
            when(jwtProvider.getClaimOfName(accessToken, "role", String.class)).thenReturn(Role.USER.name());

            // when & then
            assertThatThrownBy(
                    () -> authAdminArgumentResolver.resolveArgument(methodParameter, null, nativeWebRequest, null))
                    .isInstanceOf(ForbiddenException.class)
                    .hasMessage(ErrorTag.FORBIDDEN.getMessage());
        }

        private String generateValidAccessToken(Long accountId, Role role) {
            Date now = new Date();
            Date expiry = new Date(now.getTime() + 3600000); // 1시간

            return Jwts.builder()
                    .header()
                    .type("JWT")
                    .and()
                    .issuedAt(now)
                    .expiration(expiry)
                    .claims(Map.of("accountId", accountId, "role", role.name()))
                    .signWith(signingKey)
                    .compact();
        }
    }
}
