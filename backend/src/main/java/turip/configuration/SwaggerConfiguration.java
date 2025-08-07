package turip.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI turipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Turip")
                        .description("유튜버의 여행 정보를 한눈에 빠르게 확인할 수 있는 서비스의 REST API")
                        .version("1.0.0"));
    }
}
