package turip.container;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.MySQLContainer;

@TestConfiguration
public class TestContainerConfig {

    private static final MySQLContainer<?> MYSQL_CONTAINER;

    static {
        MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0.42")
                .withUsername("root")
                .withPassword("rootpwd")
                .withDatabaseName("test_db")
                .withReuse(true);  // 재사용으로 속도 향상

        MYSQL_CONTAINER.start();
    }

    @Bean
    @ServiceConnection
    public MySQLContainer<?> mySQLContainer() {
        return MYSQL_CONTAINER;
    }
}
