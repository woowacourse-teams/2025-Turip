package turip.auth;

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
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.UnauthorizedException;
import turip.member.domain.Account;
import turip.member.service.AccountService;
import turip.member.service.GuestService;

@Component
@RequiredArgsConstructor
public class AuthAccountArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;
    private final GuestService guestService;
    private final AccountService accountService;

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
        String deviceFid = webRequest.getHeader("device-fid");

        if (bearer != null && bearer.startsWith("Bearer ")) {
            String accessToken = bearer.substring(7); // "Bearer " 길이 = 7
            return getMemberAccount(accessToken);
        }
        return getGuestAccount(deviceFid);
    }

    private Account getMemberAccount(String accessToken) {
        try {
            Claims claims = jwtProvider.parseToken(accessToken);
            String accountId = claims.get("accountId", String.class);
            return accountService.getById(Long.valueOf(accountId));

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new UnauthorizedException(ErrorTag.ACCESS_TOKEN_SIGNATURE_NOT_VALID);
        } catch (Exception e) {
            throw new UnauthorizedException(ErrorTag.UNAUTHORIZED);
        }
    }

    private Account getGuestAccount(String deviceFid) {
        if (deviceFid == null || deviceFid.isBlank()) {
            throw new IllegalArgumentException(ErrorTag.MEMBER_NOT_FOUND);
        }
        return guestService.findOrCreateByDeviceFid(deviceFid).getAccount();
    }
}
