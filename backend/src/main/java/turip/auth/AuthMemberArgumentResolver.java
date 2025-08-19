package turip.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
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
            throw new IllegalArgumentException("회원 정보(device-fid 헤더)가 존재하지 않습니다.");
        }

        AuthMember ann = parameter.getParameterAnnotation(AuthMember.class);
        MemberResolvePolicy policy = ann.policy();

        if (policy == MemberResolvePolicy.REQUIRED) {
            return memberService.getMemberByDeviceId(deviceFid);
        }
        return memberService.findOrCreateMember(deviceFid);
    }
}
