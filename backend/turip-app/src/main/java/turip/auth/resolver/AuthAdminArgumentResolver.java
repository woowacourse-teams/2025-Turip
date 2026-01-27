package turip.auth.resolver;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.account.service.TuripMemberService;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.ForbiddenException;
import turip.common.exception.custom.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class AuthAdminArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final TuripMemberService turipMemberService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthAdmin.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String bearer = webRequest.getHeader("Authorization");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            String accessToken = bearer.substring(7); // "Bearer " 길이 = 7
            return getAdmin(accessToken);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    private TuripMember getAdmin(String accessToken) {
        try {
            Claims claims = jwtProvider.parseToken(accessToken);
            Long accountId = claims.get("accountId", Long.class);
            String roleName = claims.get("role", String.class);

            if (accountId == null || roleName == null) {
                throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
            }

            if (Role.valueOf(roleName) != Role.ADMIN) {
                throw new ForbiddenException(ErrorTag.FORBIDDEN);
            }

            return turipMemberService.getByAccountId(accountId);
        } catch (ForbiddenException e) {
            throw e;
        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_SIGNATURE_INVALID);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }
}
