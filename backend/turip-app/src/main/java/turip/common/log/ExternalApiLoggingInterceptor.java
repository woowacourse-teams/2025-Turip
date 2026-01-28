package turip.common.log;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ExternalApiLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Set<String> SENSITIVE_PARAMS = Set.of("key", "apiKey", "api_key", "token");

    private String maskSensitiveParams(URI uri) {
        String query = uri.getQuery();
        if (query == null) {
            return uri.toString();
        }
        String maskedQuery = Arrays.stream(query.split("&"))
                .map(param -> {
                    String[] kv = param.split("=", 2);
                    if (kv.length == 2 && SENSITIVE_PARAMS.contains(kv[0].toLowerCase())) {
                        return kv[0] + "=***";
                    }
                    return param;
                })
                .collect(Collectors.joining("&"));
        return uri.toString().replace(query, maskedQuery);
    }

    @Override
    @NonNull
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        long startTime = System.currentTimeMillis();

        log.info("[외부 API 요청] method: {}, uri: {}", request.getMethod(), maskSensitiveParams(request.getURI()));

        ClientHttpResponse response = execution.execute(request, body);

        long duration = System.currentTimeMillis() - startTime;

        log.info("[외부 API 응답] status: {}, duration: {}ms", response.getStatusCode(), duration);

        return response;
    }
}
