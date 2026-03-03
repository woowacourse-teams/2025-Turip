package turip.resolver;

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
import turip.common.exception.custom.IllegalArgumentException;
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
            Long accountId = jwtProvider.getClaimOfName(accessToken, "accountId", Long.class);
            String roleName = jwtProvider.getClaimOfName(accessToken, "role", String.class);

            if (roleName == null) {
                throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
            }

            if (Role.valueOf(roleName) != Role.ADMIN) {
                throw new ForbiddenException(ErrorTag.FORBIDDEN);
            }

            return turipMemberService.getByAccountId(accountId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag());
        } catch (ForbiddenException e) {
            throw e;
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
        }
    }
}
