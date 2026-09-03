package turip.common.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;

@Slf4j
@Component
public class ExternalApiLoggingFilter {

    public ExchangeFilterFunction filter() {
        return (request, next) -> {
            long startTime = System.currentTimeMillis();

            log.info("[외부 API 요청] method: {}, uri: {}",
                    request.method(), ExternalApiLogFormat.maskSensitiveParams(request.url()));

            return next.exchange(request)
                    .doOnNext(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("[외부 API 응답] status: {}, duration: {}ms", response.statusCode(), duration);
                    });
        };
    }
}
