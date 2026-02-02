package turip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = "turip",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {turip.common.configuration.WebMvcConfiguration.class}
        )
)
public class TuripAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(TuripAdminApplication.class, args);
    }
}
