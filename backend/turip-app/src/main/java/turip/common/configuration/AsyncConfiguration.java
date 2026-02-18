package turip.common.configuration;

import java.util.concurrent.Executor;
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
                        "[SSE-ThreadPool] 이벤트 전송 거부됨 - Thread pool 포화 상태 (현재 활성 스레드: {}, 잔여 큐: {})",
                        executorInstance.getActiveCount(),
                        executorInstance.getQueue().size()
                )
        );

        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);

        executor.initialize();

        return executor;
    }
}
