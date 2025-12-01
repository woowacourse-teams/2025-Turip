package turip.common.configuration;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import turip.auth.resolver.AuthAccountArgumentResolver;
import turip.auth.resolver.AuthGuestArgumentResolver;
import turip.auth.resolver.AuthMemberArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthAccountArgumentResolver authAccountArgumentResolver;
    private final AuthGuestArgumentResolver authGuestArgumentResolver;
    private final AuthMemberArgumentResolver authMemberArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authAccountArgumentResolver);
        resolvers.add(authGuestArgumentResolver);
        resolvers.add(authMemberArgumentResolver);
    }
}
