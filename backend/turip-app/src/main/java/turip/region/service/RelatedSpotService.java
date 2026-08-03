package turip.region.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;
import turip.infrastructure.client.KoreaTourismRelatedSpotClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.region.controller.dto.response.RelatedSpotsResponse;
import turip.region.domain.DomesticRegionCategory;
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
        List<RelatedSpot> relatedSpots = areaCode.getSigunguCodes().parallelStream()
                .map(sigunguCode -> koreaTourismRelatedSpotClient.searchRelatedSpots(
                        areaCode.getAreaCode(),
                        sigunguCode
                ))
                .flatMap(List::stream)
                .toList();

        return RelatedSpotsResponse.from(relatedSpots);
    }
}
