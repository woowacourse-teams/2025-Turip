package turip.auth.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import turip.account.domain.Member;
import turip.account.service.MemberService;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final MemberService memberService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthMember.class);
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
            return getMember(accessToken);
        }
        throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
    }

    private Member getMember(String accessToken) {
        try {
            Long accountId = jwtProvider.getClaimOfName(accessToken, "accountId", Long.class);
            return memberService.getByAccountId(accountId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag());
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
        }
    }
}
