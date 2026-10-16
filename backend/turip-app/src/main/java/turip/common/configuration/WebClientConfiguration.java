package turip.common.configuration;

import io.netty.channel.ChannelOption;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import turip.common.log.ExternalApiLoggingFilter;

@Configuration
@RequiredArgsConstructor
public class WebClientConfiguration {

    private static final int MAX_CONNECTIONS = 10;
    private static final int CONNECT_TIMEOUT_MILLIS = 2000;
    private static final Duration PENDING_ACQUIRE_TIMEOUT = Duration.ofSeconds(3);
    private static final Duration RESPONSE_TIMEOUT = Duration.ofSeconds(5);

    private final ExternalApiLoggingFilter loggingFilter;

    @Bean
    public WebClient baseWebClient() {
        ConnectionProvider connectionProvider = ConnectionProvider.builder("base-webclient")
                .maxConnections(MAX_CONNECTIONS)
                .pendingAcquireTimeout(PENDING_ACQUIRE_TIMEOUT)
                .build();

        HttpClient httpClient = HttpClient.create(connectionProvider)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, CONNECT_TIMEOUT_MILLIS)
                .responseTimeout(RESPONSE_TIMEOUT);

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .filter(loggingFilter.filter())
                .build();
    }
}
