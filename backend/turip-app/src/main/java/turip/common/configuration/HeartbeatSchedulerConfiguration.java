package turip.common.configuration;

import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class HeartbeatSchedulerConfiguration {

    private ScheduledExecutorService scheduledExecutorService;

    @Bean
    public ScheduledExecutorService scheduledExecutorService() {
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        return scheduledExecutorService;
    }

    @PreDestroy
    public void destroy() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            try {
                if (!scheduledExecutorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduledExecutorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduledExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }
}
