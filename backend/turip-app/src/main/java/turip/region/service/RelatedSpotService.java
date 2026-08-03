package turip.region.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.infrastructure.client.KoreaTourismRelatedSpotClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.infrastructure.client.dto.RelatedSpotResult;
import turip.region.controller.dto.response.RelatedSpotsResponse;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.RelatedTuripSpots;
import turip.region.domain.TourApiAreaCode;

@Slf4j
@Service
public class RelatedSpotService {

    private final KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;
    private final Executor koreaTourismApiExecutor;

    public RelatedSpotService(
            KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient,
            @Qualifier("koreaTourismApiExecutor") Executor koreaTourismApiExecutor) {
        this.koreaTourismRelatedSpotClient = koreaTourismRelatedSpotClient;
        this.koreaTourismApiExecutor = koreaTourismApiExecutor;
    }

    public RelatedSpotsResponse findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        TourApiAreaCode areaCode = parseToAreaCode(category);
        if (areaCode == null) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return RelatedSpotsResponse.empty();
        }

        List<RelatedSpotResult> results = getRelatedSpotsByAreaCode(areaCode);

        // API 호출이 모두 실패했거나, 성공했지만 데이터가 비어있는 경우 fallback 사용
        if (shouldUseFallback(results)) {
            RelatedTuripSpots relatedTuripSpots = RelatedTuripSpots.from(category);
            return RelatedSpotsResponse.from(relatedTuripSpots);
        }

        // 성공한 경우 API 결과 사용
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();
        return RelatedSpotsResponse.from(relatedSpots);
    }

    private TourApiAreaCode parseToAreaCode(DomesticRegionCategory category) {
        if (category == DomesticRegionCategory.OTHER_DOMESTIC) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }
        return TourApiAreaCode.fromDomesticRegionCategory(category);
    }

    private List<RelatedSpotResult> getRelatedSpotsByAreaCode(TourApiAreaCode areaCode) {
        // 여러 시군구 코드에 대해 전용 스레드풀에서 병렬로 API 호출
        List<CompletableFuture<RelatedSpotResult>> futures = areaCode.getSigunguCodes().stream()
                .map(sigunguCode -> CompletableFuture.supplyAsync(
                        () -> koreaTourismRelatedSpotClient.searchRelatedSpots(
                                areaCode.getAreaCode(),
                                sigunguCode
                        ),
                        koreaTourismApiExecutor
                ))
                .toList();

        // 모든 비동기 작업 완료 대기 (스레드풀 거부 등 예외 발생 시 실패로 처리)
        return futures.stream()
                .map(future -> {
                    try {
                        return future.join();
                    } catch (Exception e) {
                        log.warn("연관 관광지 API 호출 중 예외 발생: {}", e.getMessage());
                        return RelatedSpotResult.failure();
                    }
                })
                .toList();
    }

    private boolean shouldUseFallback(List<RelatedSpotResult> results) {
        boolean allFailed = results.stream().noneMatch(RelatedSpotResult::isSuccess);
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();

        return allFailed || relatedSpots.isEmpty();
    }
}
