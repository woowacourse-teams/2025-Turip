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
import turip.member.domain.Account;
import turip.member.service.GuestService;

@Component
@RequiredArgsConstructor
public class AuthAccountArgumentResolver implements HandlerMethodArgumentResolver {

    private final GuestService guestService;

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
        String accessToken = webRequest.getHeader("Authorization");
        String deviceFid = webRequest.getHeader("device-fid");

        if (accessToken != null) {
            return getMemberAccount(accessToken);
        }
        return getGuestAccount(deviceFid);
    }

    private Account getMemberAccount(String accessToken) {
        // 추후 구현 예정
        return null;
    }

    private Account getGuestAccount(String deviceFid) {
        if (deviceFid == null || deviceFid.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.MEMBER_NOT_FOUND);
        }
        return guestService.findOrCreateByDeviceFid(deviceFid).getAccount();
    }
}
