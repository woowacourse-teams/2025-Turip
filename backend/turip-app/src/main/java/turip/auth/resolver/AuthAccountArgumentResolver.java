package turip.auth.resolver;

import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import turip.account.domain.Account;
import turip.account.service.AccountService;
import turip.account.service.GuestService;
import turip.auth.token.JwtProvider;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class AuthAccountArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final GuestService guestService;
    private final AccountService accountService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthAccount.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        String bearer = webRequest.getHeader("Authorization");
        String deviceFid = webRequest.getHeader("device-fid");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            String accessToken = bearer.substring(7); // "Bearer " 길이 = 7
            return getMemberAccount(accessToken);
        }
        return findOrCreateGuestAccount(deviceFid);
    }

    private Account getMemberAccount(String accessToken) {
        try {
            Long accountId = jwtProvider.getClaimOfName(accessToken, "accountId", Long.class);
            return accountService.getById(accountId);
        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag());
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED, e);
        }
    }

    private Account findOrCreateGuestAccount(String deviceFid) {
        if (deviceFid == null || deviceFid.isBlank()) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
        return guestService.findOrCreateByDeviceFid(deviceFid).getAccount();
    }
}
