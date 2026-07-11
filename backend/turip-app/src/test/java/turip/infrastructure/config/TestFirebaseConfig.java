package turip.infrastructure.config;

import static org.mockito.Mockito.mock;

import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test | test-mysql")
@Configuration
public class TestFirebaseConfig {

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return mock(FirebaseMessaging.class);
    }
}
