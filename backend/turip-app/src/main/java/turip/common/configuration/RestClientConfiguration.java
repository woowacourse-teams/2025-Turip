package turip.common.configuration;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import turip.common.log.ExternalApiLoggingInterceptor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private static final Duration CONNECT_TIMEOUT = Duration.ofSeconds(5);
    private static final Duration READ_TIMEOUT = Duration.ofSeconds(10);

    private final ExternalApiLoggingInterceptor loggingInterceptor;

    @Bean
    public ClientHttpRequestFactorySettings clientHttpRequestFactorySettings() {
        return ClientHttpRequestFactorySettings.defaults()
                .withConnectTimeout(CONNECT_TIMEOUT)
                .withReadTimeout(READ_TIMEOUT);
    }

    @Bean
    public RestClient baseRestClient(RestClient.Builder builder) {
        return builder
                .requestInterceptor(loggingInterceptor)
                .build();
    }
}
