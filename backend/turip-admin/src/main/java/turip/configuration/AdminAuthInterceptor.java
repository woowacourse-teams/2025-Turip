package turip.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import turip.account.domain.Role;
import turip.auth.token.JwtProvider;
import turip.common.exception.custom.IllegalArgumentException;
import turip.common.exception.custom.UnauthorizedException;

@Component
@RequiredArgsConstructor
public class AdminAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String bearer = request.getHeader("Authorization");

        if (bearer == null || !bearer.startsWith("Bearer ")) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "인증 토큰이 필요합니다");
            return false;
        }

        String accessToken = bearer.substring(7);

        try {
            String roleName = jwtProvider.getClaimOfName(accessToken, "role", String.class);

            if (!Role.ADMIN.name().equals(roleName)) {
                sendError(response, HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 필요합니다");
                return false;
            }

            return true;

        } catch (IllegalArgumentException e) {
            throw new UnauthorizedException(e.getErrorTag());
        } catch (Exception e) {
            sendError(response, HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰입니다");
            return false;
        }
    }

    private void sendError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(
                String.format("{\"error\":\"%s\"}", message)
        );
    }
}
