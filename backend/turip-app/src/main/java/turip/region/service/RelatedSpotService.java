package turip.region.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
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

    public RelatedSpotService(KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient) {
        this.koreaTourismRelatedSpotClient = koreaTourismRelatedSpotClient;
    }

    public RelatedSpotsResponse findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        TourApiAreaCode areaCode = parseToAreaCode(category);
        if (!areaCode.isFound()) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return RelatedSpotsResponse.empty();
        }

        List<RelatedSpotResult> results = getRelatedSpotsByAreaCode(areaCode);
        return toRelatedSpotsResponse(category, results);
    }

    private TourApiAreaCode parseToAreaCode(DomesticRegionCategory category) {
        if (category == DomesticRegionCategory.OTHER_DOMESTIC) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }
        return TourApiAreaCode.fromDomesticRegionCategory(category);
    }

    private List<RelatedSpotResult> getRelatedSpotsByAreaCode(TourApiAreaCode areaCode) {
        // 가상 스레드 비교 실험: 전용 스레드풀(ThreadPoolTaskExecutor) 없이,
        // 시군구 코드별로 가상 스레드를 하나씩 띄워 병렬 블로킹 호출 (풀링이 필요 없는 가상 스레드 특성 활용)
        try (ExecutorService virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<RelatedSpotResult>> futures = areaCode.getSigunguCodes().stream()
                    .map(sigunguCode -> CompletableFuture.supplyAsync(
                            () -> koreaTourismRelatedSpotClient.searchRelatedSpots(
                                    areaCode.getAreaCode(),
                                    sigunguCode
                            ),
                            virtualThreadExecutor
                    ))
                    .toList();

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
    }

    private RelatedSpotsResponse toRelatedSpotsResponse(DomesticRegionCategory category,
                                                        List<RelatedSpotResult> results) {
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

    private boolean shouldUseFallback(List<RelatedSpotResult> results) {
        boolean allFailed = results.stream().noneMatch(RelatedSpotResult::isSuccess);
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();

        return allFailed || relatedSpots.isEmpty();
    }
}
