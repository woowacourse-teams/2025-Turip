package turip.common.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfiguration {

    @Bean(name = "sseEventExecutor")
    public Executor sseEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("SSE-EVT-");

        executor.setRejectedExecutionHandler((r, executorInstance) -> log.warn(
                        "[SSE-ThreadPool] 이벤트 전송 거부됨 - Thread pool 포화 상태 (현재 활성 스레드: {}, 잔여 큐 용량: {})",
                        executorInstance.getActiveCount(),
                        executorInstance.getQueue().remainingCapacity()
                )
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }

    @Bean(name = "fcmEventExecutor")
    public Executor fcmEventExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(4);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("FCM-EVT-");

        executor.setRejectedExecutionHandler((r, executorInstance) -> log.warn(
                        "[FCM-ThreadPool] 알림 전송 거부됨 - Thread pool 포화 상태 (현재 활성 스레드: {}, 잔여 큐 용량: {})",
                        executorInstance.getActiveCount(),
                        executorInstance.getQueue().remainingCapacity()
                )
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }

    @Bean(name = "koreaTourismApiExecutor")
    public Executor koreaTourismApiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(50);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("KOREA-TOURISM-API-");

        executor.setRejectedExecutionHandler((r, executorInstance) -> {
            log.warn(
                    "[KOREA-TOURISM-API-ThreadPool] API 호출 거부됨 - Thread pool 포화 상태 (현재 활성 스레드: {}, 잔여 큐 용량: {})",
                    executorInstance.getActiveCount(),
                    executorInstance.getQueue().remainingCapacity()
            );
            throw new RejectedExecutionException("Thread pool 포화로 인한 API 호출 거부");
        });

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        return executor;
    }
}
