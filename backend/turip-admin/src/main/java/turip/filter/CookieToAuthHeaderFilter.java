package turip.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class CookieToAuthHeaderFilter implements Filter {

    private static final String ACCESS_TOKEN_COOKIE = "accessToken";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 이미 Authorization 헤더가 있으면 그대로 사용
        String authHeader = httpRequest.getHeader(AUTHORIZATION_HEADER);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // 쿠키에서 accessToken 찾기
        Cookie[] cookies = httpRequest.getCookies();
        String token = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // 토큰이 있으면 Authorization 헤더로 변환
        if (token != null) {
            chain.doFilter(new AuthHeaderRequestWrapper(httpRequest, token), response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
