package turip.infrastructure.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import turip.region.service.RegionPopularityService;

/**
 * 지역별 관광 인기도(방문 인원수) 스냅샷 갱신 스케줄러
 * 방문자 수 데이터는 월 단위로 공개되므로 매월 1회 갱신한다.
 * 서버 재기동 등으로 스냅샷이 비어있는 경우는 최초 조회 시점의 지연 로딩(RegionPopularityService)이 커버한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegionPopularityScheduler {

    private static final String LOG_HEADER = "[한국관광 데이터랩 - 지역 인기도 스케줄러]";

    private final RegionPopularityService regionPopularityService;

    @Scheduled(cron = "0 0 4 1 * *") // 매월 1일 새벽 4시
    public void refreshMonthly() {
        log.info("{} 월간 스냅샷 갱신 시작", LOG_HEADER);
        try {
            regionPopularityService.refresh();
        } catch (Exception e) {
            log.error("{} 스냅샷 갱신 중 예외 발생: {}", LOG_HEADER, e.getMessage(), e);
        }
    }
}
