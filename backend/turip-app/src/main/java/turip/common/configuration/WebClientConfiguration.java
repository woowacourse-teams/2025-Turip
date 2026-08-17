package turip.common.configuration;

import io.netty.channel.ChannelOption;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

@Slf4j
@Configuration
public class WebClientConfiguration {

    private static final Set<String> SENSITIVE_PARAMS = Set.of("key", "apikey", "api_key", "token");
    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECT_TIMEOUT_MILLIS = 5000;

    @Bean
    public WebClient koreaTourismWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("korea-tourism-webclient")
                .maxConnections(MAX_CONNECTIONS)
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(loggingFilter())
                .build();
    }

    private ExchangeFilterFunction loggingFilter() {
        return (request, next) -> {
            long startTime = System.currentTimeMillis();
            log.info("[외부 API 요청] method: {}, uri: {}", request.method(), maskSensitiveParams(request.url()));

            return next.exchange(request)
                    .doOnNext(response -> {
                        long duration = System.currentTimeMillis() - startTime;
                        log.info("[외부 API 응답] status: {}, duration: {}ms", response.statusCode(), duration);
                    });
        };
    }

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
}
