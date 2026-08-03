package turip.region.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class RelatedSpotService {

    private final KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    public RelatedSpotsResponse findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        if (category == DomesticRegionCategory.OTHER_DOMESTIC) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }

        TourApiAreaCode areaCode = TourApiAreaCode.fromDomesticRegionCategory(category);

        if (!areaCode.isFound()) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return RelatedSpotsResponse.empty();
        }

        // 여러 시군구 코드에 대해 병렬로 API 호출
        List<RelatedSpotResult> results = areaCode.getSigunguCodes().parallelStream()
                .map(sigunguCode -> koreaTourismRelatedSpotClient.searchRelatedSpots(
                        areaCode.getAreaCode(),
                        sigunguCode
                )).toList();

        // API 호출이 모두 실패했거나, 성공했지만 데이터가 비어있는 경우 fallback 사용
        boolean anySuccess = results.stream().anyMatch(RelatedSpotResult::isSuccess);
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();

        if (!anySuccess || relatedSpots.isEmpty()) {
            RelatedTuripSpots relatedTuripSpots = RelatedTuripSpots.from(category);
            return RelatedSpotsResponse.from(relatedTuripSpots);
        }

        return RelatedSpotsResponse.from(relatedSpots);
    }
}
