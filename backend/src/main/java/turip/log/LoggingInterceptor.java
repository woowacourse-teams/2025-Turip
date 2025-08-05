package turip.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String REQUEST_ID_ATTRIBUTE = "traceId";

    @Override
    public boolean preHandle(final HttpServletRequest request, final HttpServletResponse response, final Object handler)
            throws Exception {
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        request.setAttribute(REQUEST_ID_ATTRIBUTE, traceId);
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());

        MDC.put("traceId", traceId);

        log.info("API 요청",
                "method", request.getMethod(),
                "uri", request.getRequestURI());
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            if (ex != null) {
                log.error("API 처리 중 예외 발생",
                        "method", request.getMethod(),
                        "uri", request.getRequestURI(),
                        "exceptionMessage", ex.getMessage(),
                        "httpStatus", response.getStatus());

            } else {
                log.info("API 처리 성공",
                        "method", request.getMethod(),
                        "uri", request.getRequestURI(),
                        "durationMs", duration,
                        "httpStatus", response.getStatus());
            }
        } else {
            log.warn("요청 시작 시간을 찾을 수 없음",
                    "method", request.getMethod(),
                    "uri", request.getRequestURI(),
                    "httpStatus", response.getStatus());
        }

        MDC.remove("traceId");
    }
}
