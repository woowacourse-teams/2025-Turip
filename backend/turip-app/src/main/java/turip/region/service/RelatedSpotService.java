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
import turip.region.service.dto.RelatedSpotsDataSource;
import turip.region.service.dto.RelatedSpotsResult;

@Slf4j
@Service
public class RelatedSpotService {

    private final KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    public RelatedSpotService(KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient) {
        this.koreaTourismRelatedSpotClient = koreaTourismRelatedSpotClient;
    }

    public RelatedSpotsResult findRelatedSpotsByRegionCategory(DomesticRegionCategory category) {
        TourApiAreaCode areaCode = parseToAreaCode(category);
        if (!areaCode.isFound()) {
            log.warn("지역 카테고리에 매핑되는 TourAPI 지역 코드를 찾을 수 없습니다: {}", category);
            return RelatedSpotsResult.of(
                    RelatedSpotsResponse.empty(),
                    RelatedSpotsDataSource.NOT_REQUESTED
            );
        }

        List<RelatedSpotResult> results = getRelatedSpotsByAreaCode(areaCode);
        return toRelatedSpotsResult(category, results);
    }

    private TourApiAreaCode parseToAreaCode(DomesticRegionCategory category) {
        if (category == DomesticRegionCategory.OTHER_DOMESTIC) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }
        return TourApiAreaCode.fromDomesticRegionCategory(category);
    }

    /**
     * 시군구 코드별로 외부 API를 병렬 호출한다 (부하테스트 조건 3: 가상 스레드).
     * <p>
     * 조건 1과 비교했을 때 바뀐 것은 "병렬 실행 수단" 하나뿐이다. 외부 호출이 블로킹이라는 점도,
     * 호출부인 톰캣 스레드가 결과를 기다린다는 점도 같다. 따라서 두 조건의 차이는 고정 크기
     * 스레드풀과 가상 스레드의 차이로만 설명된다.
     * <p>
     * 가상 스레드는 생성 비용이 낮아 풀링하지 않고 호출마다 새로 띄운다. 블로킹되면 캐리어
     * 스레드를 반납하고 언마운트되므로, 조건 1처럼 풀 크기가 동시 호출 수의 상한이 되지 않는다.
     * <p>
     * spring.threads.virtual.enabled는 켜지 않는다. 그 플래그는 톰캣 워커까지 가상 스레드로
     * 바꾸기 때문에, 켜면 바뀌는 변수가 둘이 되어 가상 스레드 자체의 효과를 분리할 수 없다.
     * 여기서 쓰는 Executors.newVirtualThreadPerTaskExecutor()는 JDK API라 그 플래그와 무관하게
     * 동작한다.
     */
    private List<RelatedSpotResult> getRelatedSpotsByAreaCode(TourApiAreaCode areaCode) {
        // close()가 제출된 작업의 완료를 기다리므로 try-with-resources가 곧 완료 대기 지점이 된다.
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
