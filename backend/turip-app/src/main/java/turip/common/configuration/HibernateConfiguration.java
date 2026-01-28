package turip.common.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import turip.common.querycount.QueryCountInspector;

@Slf4j
@Configuration
@RequiredArgsConstructor
@Profile({"local", "dev"})
public class HibernateConfiguration {

    private final QueryCountInspector queryCountInspector;

    @Bean
    public HibernatePropertiesCustomizer configureStatementInspector() {
        return hibernateProperties -> {
            hibernateProperties.put(AvailableSettings.STATEMENT_INSPECTOR, queryCountInspector);
        };
    }
}
