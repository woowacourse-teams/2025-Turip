package turip.infrastructure.client;

import static turip.infrastructure.client.KoreaTourismApiConstants.MOBILE_APP;
import static turip.infrastructure.client.KoreaTourismApiConstants.MOBILE_OS;
import static turip.infrastructure.client.KoreaTourismApiConstants.RESPONSE_TYPE;

import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.KoreaTourismVisitorResponse;
import turip.infrastructure.client.dto.KoreaTourismVisitorResponse.VisitorItem;
import turip.infrastructure.client.dto.VisitorFetchResult;

/**
 * 한국관광 데이터랩 지역별 방문자 수 API 클라이언트 - 광역(metco): 시도 단위 방문자 수 (지역 필터 없이 전체 시도 반환) - 기초(locgo): 시군구 단위 방문자 수 (signguCd로 필터)
 */
@Slf4j
@Component
public class KoreaTourismVisitorClient {

    private static final int DEFAULT_NUM_OF_ROWS = 1000;
    private static final int MAX_PAGE = 50;
    private static final int MAX_MONTH_TO_CHECK = 6;
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter YMD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final RestClient restClient;
    private final String serviceKey;
    private final String metcoUrl;
    private final String locgoUrl;

    public KoreaTourismVisitorClient(RestClient baseRestClient,
                                     @Value("${korea-tourism.api.key}") String serviceKey,
                                     @Value("${korea-tourism.visitor-api.metco-url}") String metcoUrl,
                                     @Value("${korea-tourism.visitor-api.locgo-url}") String locgoUrl) {
        this.restClient = baseRestClient;
        this.serviceKey = serviceKey;
        this.metcoUrl = metcoUrl;
        this.locgoUrl = locgoUrl;
    }

    /**
     * 광역(시도) 방문자 수를 조회한다. 지역 필터가 없어 전체 시도가 한 번에 반환된다.
     */
    public VisitorFetchResult fetchProvinceVisitors(String startYmd, String endYmd) {
        return fetchAllPages(pageNo -> URI.create(new StringBuilder(metcoUrl)
                .append("?serviceKey=").append(serviceKey)
                .append("&pageNo=").append(pageNo)
                .append("&numOfRows=").append(DEFAULT_NUM_OF_ROWS)
                .append("&MobileOS=").append(MOBILE_OS)
                .append("&MobileApp=").append(MOBILE_APP)
                .append("&_type=").append(RESPONSE_TYPE)
                .append("&startYmd=").append(startYmd)
                .append("&endYmd=").append(endYmd)
                .toString()));
    }

    /**
     * 기초(시군구) 방문자 수를 조회한다. 지역 필터가 없어 전체 시군구가 반환되므로 호출부에서 signguCode로 필터한다.
     */
    public VisitorFetchResult fetchCityVisitors(String startYmd, String endYmd) {
        return fetchAllPages(pageNo -> URI.create(new StringBuilder(locgoUrl)
                .append("?serviceKey=").append(serviceKey)
                .append("&pageNo=").append(pageNo)
                .append("&numOfRows=").append(DEFAULT_NUM_OF_ROWS)
                .append("&MobileOS=").append(MOBILE_OS)
                .append("&MobileApp=").append(MOBILE_APP)
                .append("&_type=").append(RESPONSE_TYPE)
                .append("&startYmd=").append(startYmd)
                .append("&endYmd=").append(endYmd)
                .toString()));
    }

    /**
     * 데이터가 존재하는 최신 완결 월을 현재 월부터 역순으로 탐색한다. 방문자 수 데이터는 1~2개월 지연되어 공개되므로 가장 최근 가용 월을 동적으로 찾는다.
     *
     * @return 데이터가 존재하는 최신 월 (yyyyMM), 찾지 못하면 Optional.empty()
     */
    public Optional<String> findLatestBaseMonth() {
        YearMonth currentMonth = YearMonth.now();

        for (int i = 0; i < MAX_MONTH_TO_CHECK; i++) {
            YearMonth candidate = currentMonth.minusMonths(i);
            String firstDay = candidate.atDay(1).format(YMD_FORMATTER);

            try {
                URI uri = URI.create(new StringBuilder(metcoUrl)
                        .append("?serviceKey=").append(serviceKey)
                        .append("&pageNo=").append(1)
                        .append("&numOfRows=").append(1)
                        .append("&MobileOS=").append(MOBILE_OS)
                        .append("&MobileApp=").append(MOBILE_APP)
                        .append("&_type=").append(RESPONSE_TYPE)
                        .append("&startYmd=").append(firstDay)
                        .append("&endYmd=").append(firstDay)
                        .toString());

                KoreaTourismVisitorResponse response = restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(KoreaTourismVisitorResponse.class);

                if (response != null && response.isSuccess() && !response.getVisitorItems().isEmpty()) {
                    String baseMonth = candidate.format(YEAR_MONTH_FORMATTER);
                    log.info("방문자 수 최신 완결 월 발견: {}", baseMonth);
                    return Optional.of(baseMonth);
                }
            } catch (Exception e) {
                log.info("방문자 수 기준월 {} 탐색 실패: {}", candidate.format(YEAR_MONTH_FORMATTER), e.getMessage());
            }
        }

        log.warn("방문자 수 최신 완결 월을 찾지 못했습니다.");
        return Optional.empty();
    }

    private VisitorFetchResult fetchAllPages(PageUriFactory uriFactory) {
        List<VisitorItem> collected = new ArrayList<>();
        try {
            for (int pageNo = 1; pageNo <= MAX_PAGE; pageNo++) {
                KoreaTourismVisitorResponse response = restClient.get()
                        .uri(uriFactory.create(pageNo))
                        .retrieve()
                        .body(KoreaTourismVisitorResponse.class);

                if (response == null || !response.isSuccess()) {
                    log.warn("방문자 수 API 응답 실패: resultCode={}, resultMsg={}",
                            response == null ? null : response.getResultCode(),
                            response == null ? null : response.getResultMsg());
                    return VisitorFetchResult.failure();
                }

                List<VisitorItem> items = response.getVisitorItems();
                collected.addAll(items);

                Integer totalCount = response.getTotalCount();
                if (items.isEmpty() || totalCount == null || collected.size() >= totalCount) {
                    break;
                }
            }
            return VisitorFetchResult.success(collected);
        } catch (Exception e) {
            log.warn("방문자 수 API 호출 실패", e);
            return VisitorFetchResult.failure();
        }
    }

    @FunctionalInterface
    private interface PageUriFactory {
        URI create(int pageNo);
    }
}
