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
import turip.region.service.dto.RelatedSpotsDataSource;
import turip.region.service.dto.RelatedSpotsResult;

@Slf4j
@Service
public class RelatedSpotService {

    private final KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    public RelatedSpotService(KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient) {
        this.koreaTourismRelatedSpotClient = koreaTourismRelatedSpotClient;
    }

    public Mono<RelatedSpotsResult> findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        TourApiAreaCode areaCode = parseToAreaCode(category);
        if (!areaCode.isFound()) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return Mono.just(RelatedSpotsResult.of(
                    RelatedSpotsResponse.empty(),
                    RelatedSpotsDataSource.NOT_REQUESTED
            ));
        }

        return getRelatedSpotsByAreaCode(areaCode)
                .map(results -> toRelatedSpotsResult(category, results));
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

        return Mono.zip(monos, results -> Arrays.stream(results)
                .map(result -> (RelatedSpotResult) result)
                .toList());
    }

    private RelatedSpotsResult toRelatedSpotsResult(DomesticRegionCategory category,
                                                    List<RelatedSpotResult> results) {
        RelatedSpotsDataSource dataSource = resolveDataSource(results);

        // API 호출이 모두 실패했거나, 성공했지만 데이터가 비어있는 경우 fallback 사용
        if (dataSource.isFallback()) {
            RelatedTuripSpots relatedTuripSpots = RelatedTuripSpots.from(category);
            return RelatedSpotsResult.of(RelatedSpotsResponse.from(relatedTuripSpots), dataSource);
        }

        // 성공한 경우 API 결과 사용
        List<RelatedSpot> relatedSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .toList();
        return RelatedSpotsResult.of(RelatedSpotsResponse.from(relatedSpots), dataSource);
    }

    /**
     * 응답 데이터의 출처를 판정한다.
     * <p>
     * 폴백으로 넘어가는 사유를 "외부 API 전부 실패"와 "응답은 받았지만 데이터가 빔"으로 나눈다.
     * 부하테스트에서 전자는 호출량 제한이나 장애로 차단된 상황을, 후자는 해당 지역에 데이터가
     * 없는 상황을 의미하므로, 둘을 섞어 집계하면 부하로 인한 차단 비율을 알 수 없다.
     */
    private RelatedSpotsDataSource resolveDataSource(List<RelatedSpotResult> results) {
        boolean allFailed = results.stream().noneMatch(RelatedSpotResult::isSuccess);
        if (allFailed) {
            return RelatedSpotsDataSource.FALLBACK_FAILED;
        }

        boolean noSpots = results.stream()
                .flatMap(result -> result.spots().stream())
                .findAny()
                .isEmpty();
        if (noSpots) {
            return RelatedSpotsDataSource.FALLBACK_EMPTY;
        }

        return RelatedSpotsDataSource.API;
    }
}
