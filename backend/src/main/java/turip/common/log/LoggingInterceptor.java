package turip.common.log;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@Profile("dev")
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_START_TIME_ATTRIBUTE = "requestStartTime";
    private static final String DURATION_TIME_UNIT = "ms";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if ("/error".equals(request.getRequestURI())) {
            return true;
        }
        request.setAttribute(REQUEST_START_TIME_ATTRIBUTE, System.currentTimeMillis());

        MDC.put("device_fid", request.getHeader("device-fid"));
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());

        log.info("API 요청");
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;

            MDC.put("httpStatus", String.valueOf(response.getStatus()));
            MDC.put("duration", duration + DURATION_TIME_UNIT);

            if (ex != null) {
                log.error("API 처리 중 예외 발생: {}", ex.getMessage(), ex);

            } else {
                log.info("API 응답");
            }
        } else {
            log.warn("요청 시작 시간을 찾을 수 없음");
        }

        MDC.remove("traceId");
        MDC.remove("device_fid");
        MDC.remove("method");
        MDC.remove("uri");
        MDC.remove("httpStatus");
        MDC.remove("duration");
    }
}
