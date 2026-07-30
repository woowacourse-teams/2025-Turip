package turip.infrastructure.client;

import java.net.URI;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;

@Slf4j
@Component
public class KoreaTourismRelatedSpotClient {

    private static final String MOBILE_OS = "ETC";
    private static final String MOBILE_APP = "Turip";
    private static final String RESPONSE_TYPE = "json";
    private static final int DEFAULT_NUM_OF_ROWS = 30;
    private static final int DEFAULT_PAGE_NO = 1;
    private static final String BASE_YM = "202606"; // API 제공 데이터 최신 기준월

    private final RestClient restClient;
    private final String koreaTourismApiKey;
    private final String koreaTourismApiUrl;

    public KoreaTourismRelatedSpotClient(RestClient baseRestClient,
                                         @Value("${korea-tourism.api.key}") String koreaTourismApiKey,
                                         @Value("${korea-tourism.api.url}") String koreaTourismApiUrl) {
        this.restClient = baseRestClient;
        this.koreaTourismApiKey = koreaTourismApiKey;
        this.koreaTourismApiUrl = koreaTourismApiUrl;
    }

    /**
     * 지역 코드와 시군구 코드 기반으로 연관 관광지 목록을 조회한다
     *
     * @param areaCode    지역 코드 (시도 단위)
     * @param sigunguCode 시군구 코드
     * @return 연관 관광지 목록
     */
    public List<RelatedSpot> searchRelatedSpots(int areaCode, int sigunguCode) {
        try {
            // URI를 완전히 수동으로 생성 (이중 인코딩 방지)
            StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                    .append("?serviceKey=").append(koreaTourismApiKey)
                    .append("&pageNo=").append(DEFAULT_PAGE_NO)
                    .append("&numOfRows=").append(DEFAULT_NUM_OF_ROWS)
                    .append("&MobileOS=").append(MOBILE_OS)
                    .append("&MobileApp=").append(MOBILE_APP)
                    .append("&_type=").append(RESPONSE_TYPE)
                    .append("&baseYm=").append(BASE_YM)
                    .append("&areaCd=").append(areaCode)
                    .append("&signguCd=").append(sigunguCode);

            URI uri = URI.create(uriString.toString());
            KoreaTourismRelatedSpotResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KoreaTourismRelatedSpotResponse.class);

            if (response == null || !response.isSuccess()) {
                log.warn("한국관광공사 연관 관광지 API 응답 실패: response={}", response);
                return List.of();
            }

            return response.getRelatedSpots();
        } catch (Exception e) {
            log.warn("한국관광공사 연관 관광지 API 호출 실패: areaCode={}, sigunguCode={}, message={}",
                    areaCode,
                    sigunguCode,
                    e.getMessage());
            return List.of();
        }
    }
}
