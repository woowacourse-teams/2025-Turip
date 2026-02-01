package turip.common.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import turip.common.log.ExternalApiLoggingInterceptor;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final ExternalApiLoggingInterceptor loggingInterceptor;

    // TODO: api 타임아웃 설정하기
    @Bean
    public RestClient baseRestClient(RestClient.Builder builder) {
        return builder
                .requestInterceptor(loggingInterceptor)
                .build();
    }
}
