package turip.common.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration {

    private static final String BEARER_AUTH = "BearerAuth";

    @Bean
    public OpenAPI turipOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Turip")
                        .description("유튜버의 여행 정보를 한눈에 빠르게 확인할 수 있는 서비스의 REST API")
                        .version("1.0.0"))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT 인증 토큰 (선택 사항)")
                        )
                );
    }

    @Bean
    public OpenApiCustomizer globalHeaderCustomizer() {
        return openApi -> openApi.getPaths().values().forEach(pathItem ->
                pathItem.readOperations().forEach(operation -> {
                    // device-fid 헤더 추가 (필수)
                    operation.addParametersItem(
                            new Parameter()
                                    .in("header")
                                    .required(true)
                                    .name("device-fid")
                                    .description("디바이스 식별자")
                                    .schema(new StringSchema())
                    );

                    // Authorization 헤더 추가 (선택적)
                    // SecurityRequirement를 빈 리스트로 추가하면 선택적으로 적용됨
                    operation.addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH));
                })
        );
    }
}
