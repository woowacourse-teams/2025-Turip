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

    /**
     * 연관 관광지 외부 API 호출 전용 스레드풀 (부하테스트 조건 1).
     * <p>
     * 요청 1건당 시군구 코드 3개를 병렬로 호출하므로, 풀 크기 10은 동시에 처리 가능한
     * 외부 호출이 10개라는 뜻이다. 호출이 블로킹되는 동안 스레드가 점유되기 때문에
     * 동시 요청이 늘어나면 큐에서 대기하게 되고, 이 지점이 처리량의 상한이 된다.
     * 풀이 포화되면 예외를 던져 폴백으로 떨어지게 한다.
     */
    @Bean(name = "koreaTourismApiExecutor")
    public Executor koreaTourismApiExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
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
}
