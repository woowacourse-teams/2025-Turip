package turip.common.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfiguration {
    // TODO: 타임아웃 설정하기
    @Bean
    public RestClient baseRestClient(RestClient.Builder builder) {
        return builder.build();
    }
}
