package turip.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.member.service.MemberService;

@Component
@RequiredArgsConstructor
public class AuthMemberArgumentResolver implements HandlerMethodArgumentResolver {

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
        String deviceFid = webRequest.getHeader("device-fid");
        if (deviceFid == null || deviceFid.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.MEMBER_NOT_FOUND);
        }

        AuthMember ann = parameter.getParameterAnnotation(AuthMember.class);
        MemberResolvePolicy policy = ann.policy();

        if (policy == MemberResolvePolicy.REQUIRED) {
            return memberService.getMemberByDeviceId(deviceFid);
        }
        return memberService.findOrCreateMember(deviceFid);
    }
}
