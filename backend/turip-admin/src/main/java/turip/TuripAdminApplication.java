package turip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import turip.common.configuration.WebMvcConfiguration;

@SpringBootApplication
@ComponentScan(
    basePackages = "turip",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = {WebMvcConfiguration.class}
    )
)
public class TuripAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuripAdminApplication.class, args);
    }
}
