package turip.container;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
@ConditionalOnClass(MySQLContainer.class)
public class TestContainerConfig {

    @Bean
    @ServiceConnection
    public MySQLContainer mySQLContainer() {
        return new MySQLContainer("mysql:8.0.42")
                .withUsername("root")
                .withPassword("rootpwd")
                .withDatabaseName("test_db");
    }
}
