package turip.infrastructure.client;

import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse;
import turip.infrastructure.client.dto.RelatedSpotResult;

@Slf4j
@Component
public class KoreaTourismRelatedSpotClient {

    private static final String MOBILE_OS = "ETC";
    private static final String MOBILE_APP = "Turip";
    private static final String RESPONSE_TYPE = "json";
    private static final int DEFAULT_NUM_OF_ROWS = 30;
    private static final int DEFAULT_PAGE_NO = 1;
    private static final String DEFAULT_BASE_YM = "202606"; // 초기 기준월
    private static final int MAX_MONTH_TO_CHECK = 6; // 최대 6개월 전까지 확인
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

    private final RestClient restClient;
    private final WebClient webClient;
    private final String koreaTourismApiKey;
    private final String koreaTourismApiUrl;

    private volatile String currentBaseYm = DEFAULT_BASE_YM; // 동적으로 관리되는 기준월

    public KoreaTourismRelatedSpotClient(RestClient baseRestClient,
                                         WebClient baseWebClient,
                                         @Value("${korea-tourism.api.key}") String koreaTourismApiKey,
                                         @Value("${korea-tourism.api.url}") String koreaTourismApiUrl) {
        this.restClient = baseRestClient;
        this.webClient = baseWebClient;
        this.koreaTourismApiKey = koreaTourismApiKey;
        this.koreaTourismApiUrl = koreaTourismApiUrl;
    }

    public Mono<RelatedSpotResult> searchRelatedSpots(int areaCode, int sigunguCode) {
        // URI를 완전히 수동으로 생성 (이중 인코딩 방지)
        StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                .append("?serviceKey=").append(koreaTourismApiKey)
                .append("&pageNo=").append(DEFAULT_PAGE_NO)
                .append("&numOfRows=").append(DEFAULT_NUM_OF_ROWS)
                .append("&MobileOS=").append(MOBILE_OS)
                .append("&MobileApp=").append(MOBILE_APP)
                .append("&_type=").append(RESPONSE_TYPE)
                .append("&baseYm=").append(currentBaseYm)
                .append("&areaCd=").append(areaCode)
                .append("&signguCd=").append(sigunguCode);

        URI uri = URI.create(uriString.toString());

        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(KoreaTourismRelatedSpotResponse.class)
                .switchIfEmpty(Mono.fromRunnable(() ->
                        log.warn("한국관광공사 연관 관광지 API 응답 본문이 비어있음: areaCode={}, sigunguCode={}",
                                areaCode, sigunguCode)))
                .map(response -> {
                    if (!response.isSuccess()) {
                        log.warn("한국관광공사 연관 관광지 API 응답 실패: resultCode={}, resultMsg={}",
                                response.getResultCode(), response.getResultMsg());
                        return RelatedSpotResult.failure();
                    }
                    // API 호출 성공, 데이터가 비어있어도 success로 반환
                    return RelatedSpotResult.success(response.getRelatedSpots());
                })
                .defaultIfEmpty(RelatedSpotResult.failure())
                .onErrorResume(e -> {
                    log.warn("한국관광공사 연관 관광지 API 호출 실패: areaCode={}, sigunguCode={}, message={}",
                            areaCode, sigunguCode, e.getMessage());
                    return Mono.just(RelatedSpotResult.failure());
                });
    }

    /**
     * 최신 기준월을 찾아서 반환합니다. 현재 월부터 최대 MAX_MONTH_TO_CHECK개월 전까지 역순으로 시도합니다.
     *
     * @param areaCode    지역 코드
     * @param sigunguCode 시군구 코드
     * @return 최신 기준월 (YYYYMM 형식), 찾지 못하면 null
     */
    public String findLatestBaseYm(int areaCode, int sigunguCode) {
        YearMonth currentMonth = YearMonth.now();

        for (int i = 0; i < MAX_MONTH_TO_CHECK; i++) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            String baseYm = targetMonth.format(YEAR_MONTH_FORMATTER);

            // 현재 기준월보다 작으면 더 이상 확인 불필요
            if (baseYm.compareTo(currentBaseYm) < 0) {
                break;
            }

            try {
                StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                        .append("?serviceKey=").append(koreaTourismApiKey)
                        .append("&pageNo=").append(DEFAULT_PAGE_NO)
                        .append("&numOfRows=").append(1) // 1개만 조회
                        .append("&MobileOS=").append(MOBILE_OS)
                        .append("&MobileApp=").append(MOBILE_APP)
                        .append("&_type=").append(RESPONSE_TYPE)
                        .append("&baseYm=").append(baseYm)
                        .append("&areaCd=").append(areaCode)
                        .append("&signguCd=").append(sigunguCode);

                URI uri = URI.create(uriString.toString());
                KoreaTourismRelatedSpotResponse response = restClient.get()
                        .uri(uri)
                        .retrieve()
                        .body(KoreaTourismRelatedSpotResponse.class);

                // API 응답 성공이면 해당 기준월이 유효한 것으로 판단 (데이터 유무 무관)
                if (response != null && response.isSuccess()) {
                    log.info("최신 기준월 발견: {}", baseYm);
                    return baseYm;
                }
            } catch (Exception e) {
                log.info("기준월 {} 시도 실패: {}", baseYm, e.getMessage());
            }
        }

        log.warn("최신 기준월을 찾을 수 없습니다. 기본값 사용: {}", DEFAULT_BASE_YM);
        return null;
    }

    public void updateBaseYm(String baseYm) {
        if (baseYm != null && !baseYm.equals(currentBaseYm)) {
            String oldBaseYm = currentBaseYm;
            currentBaseYm = baseYm;
            log.info("기준월 업데이트: {} -> {}", oldBaseYm, baseYm);
        }
    }

    public String getCurrentBaseYm() {
        return currentBaseYm;
    }
}
