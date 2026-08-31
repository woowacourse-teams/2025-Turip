package turip.region.service;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
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

    public Mono<RelatedSpotsResponse> findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        TourApiAreaCode areaCode = parseToAreaCode(category);
        if (!areaCode.isFound()) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return Mono.just(RelatedSpotsResponse.empty());
        }

        return getRelatedSpotsByAreaCode(areaCode)
                .map(results -> toResponse(category, results));
    }

    private RelatedSpotsResponse toResponse(DomesticRegionCategory category, List<RelatedSpotResult> results) {
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

    private Mono<List<RelatedSpotResult>> getRelatedSpotsByAreaCode(TourApiAreaCode areaCode) {
        // 여러 시군구 코드에 대해 WebClient로 비동기 논블로킹 병렬 호출
        List<Mono<RelatedSpotResult>> monos = areaCode.getSigunguCodes().stream()
                .map(sigunguCode -> koreaTourismRelatedSpotClient.searchRelatedSpots(
                        areaCode.getAreaCode(),
                        sigunguCode
                ))
                .toList();

        // 시군구 코드가 1개뿐인 지역(강릉/속초/경주 등)은 zip 없이 바로 매핑
        if (monos.size() == 1) {
            return monos.getFirst().map(List::of);
        }

        return Mono.zip(monos, results -> Arrays.stream(results)
                .map(result -> (RelatedSpotResult) result)
                .toList());
    }

    private boolean shouldUseFallback(List<RelatedSpotResult> results) {
        boolean allFailed = results.stream().noneMatch(RelatedSpotResult::isSuccess);
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();

        return allFailed || relatedSpots.isEmpty();
    }
}
