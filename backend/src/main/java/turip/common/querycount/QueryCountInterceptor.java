package turip.common.querycount;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class QueryCountInterceptor implements HandlerInterceptor {

    private static final String QUERY_COUNT_LOG_FORMAT = "QUERY_COUNT: {}";
    private static final String QUERY_COUNT_WARNING_LOG_FORMAT = "message: [QUERY_COUNT_WARNING] 하나의 요청에 쿼리가 {}번 발생했습니다.";
    private static final int QUERY_COUNT_WARNING_STANDARD = 10;

    private final QueryCountInspector queryCountInspector;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        queryCountInspector.startCounter();
        return true;
    }

    @Override
    public void afterCompletion(final HttpServletRequest request, final HttpServletResponse response,
                                final Object handler, final Exception ex) {
        QueryCounter queryCounter = queryCountInspector.getQueryCount();
        final long queryCount = queryCounter.getCount();

        log.info(QUERY_COUNT_LOG_FORMAT, queryCount);
        if (queryCount >= QUERY_COUNT_WARNING_STANDARD) {
            log.info(QUERY_COUNT_WARNING_LOG_FORMAT, queryCount);
        }
        queryCountInspector.clearCounter();
    }
}
