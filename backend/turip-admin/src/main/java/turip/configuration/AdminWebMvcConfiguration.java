package turip.configuration;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import turip.resolver.AuthAdminArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class AdminWebMvcConfiguration implements WebMvcConfigurer {

    private final AuthAdminArgumentResolver authAdminArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authAdminArgumentResolver);
    }
}
