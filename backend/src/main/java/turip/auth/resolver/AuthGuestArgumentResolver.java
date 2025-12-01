package turip.auth.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.member.service.GuestService;

@Component
@RequiredArgsConstructor
public class AuthGuestArgumentResolver implements HandlerMethodArgumentResolver {

    private final GuestService guestService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthGuest.class);
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
            throw new BadRequestException(ErrorTag.DEVICE_FID_REQUIRED);
        }
        return guestService.findOrCreateByDeviceFid(deviceFid);
    }
}
