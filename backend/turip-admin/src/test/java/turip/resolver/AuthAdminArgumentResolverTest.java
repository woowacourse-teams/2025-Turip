package turip.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.account.service.TuripMemberService;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;

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

    @DisplayName("resolveArgument 메서드 테스트")
    @Nested
    class ResolveArgumentTest {

        @DisplayName("ADMIN role을 가진 유효한 토큰으로 TuripMember를 반환한다")
        @Test
        void resolveArgumentWithAdminRole() {
            // given
            Long accountId = 1L;
            String accessToken = "accessToken";
            String bearerToken = "Bearer " + accessToken;
            TuripMember turipMember = new TuripMember(1L, null, "turip", "ValidPass1!");

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
            String accessToken = "accessToken";
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
    }
}
