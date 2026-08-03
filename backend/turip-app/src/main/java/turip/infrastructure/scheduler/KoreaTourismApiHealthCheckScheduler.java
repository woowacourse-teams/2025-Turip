package turip.infrastructure.scheduler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import turip.infrastructure.client.KoreaTourismRelatedSpotClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.region.domain.TourApiAreaCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class KoreaTourismApiHealthCheckScheduler {

    private static final String LOG_HEADER = "[한국관광공사 - 연관관광지 API 스케줄러]";

    private final KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    @Scheduled(cron = "0 0 0 * * *") // 매일 자정
    public void checkKoreaTourismApiHealth() {
        log.info("{} 헬스체크 시작", LOG_HEADER);

        try {
            // 1. 현재 기준월로 헬스체크 (API 살아있는지 확인)
            List<RelatedSpot> spots = koreaTourismRelatedSpotClient.searchRelatedSpots(
                    TourApiAreaCode.SEOUL.getAreaCode(),
                    TourApiAreaCode.SEOUL.getSigunguCodes().get(0)
            );

            if (spots.isEmpty()) {
                log.error("{} 헬스체크 실패 - API 응답 없음", LOG_HEADER);
                return;
            }

            log.info("{} 헬스체크 성공 - 기준월: {}, 응답 데이터 수: {}",
                    LOG_HEADER, koreaTourismRelatedSpotClient.getCurrentBaseYm(), spots.size());

            // 2. 최신 기준월 찾기
            log.info("{} 최신 기준월 탐색 시작", LOG_HEADER);
            String latestBaseYm = koreaTourismRelatedSpotClient.findLatestBaseYm(
                    TourApiAreaCode.SEOUL.getAreaCode(),
                    TourApiAreaCode.SEOUL.getSigunguCodes().get(0)
            );

            if (latestBaseYm != null) {
                koreaTourismRelatedSpotClient.updateBaseYm(latestBaseYm);
                log.info("{} 기준월 업데이트 완료 - 새 기준월: {}", LOG_HEADER, latestBaseYm);
            } else {
                log.error("{} 최신 기준월을 찾을 수 없습니다", LOG_HEADER);
            }
        } catch (Exception e) {
            log.error("{} 예외 발생: {}", LOG_HEADER, e.getMessage(), e);
        }
    }
}
