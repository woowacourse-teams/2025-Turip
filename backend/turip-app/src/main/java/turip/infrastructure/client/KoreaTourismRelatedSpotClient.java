package turip.infrastructure.client;

import java.net.URI;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.region.domain.TourApiAreaCode;

@Slf4j
@Component
public class KoreaTourismRelatedSpotClient {

    private static final String MOBILE_OS = "ETC";
    private static final String MOBILE_APP = "Turip";
    private static final String RESPONSE_TYPE = "json";
    private static final int DEFAULT_NUM_OF_ROWS = 50;
    private static final int DEFAULT_PAGE_NO = 1;
    private static final DateTimeFormatter BASE_YM_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");

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
     * 지역 코드 기반으로 연관 관광지 목록을 조회한다
     *
     * @param tourApiAreaCode TourAPI 지역 코드
     * @return 연관 관광지 목록
     */
    public List<RelatedSpot> searchRelatedSpots(TourApiAreaCode tourApiAreaCode) {
        String baseYm = getPreviousYearMonth();
        // URI를 완전히 수동으로 생성 (이중 인코딩 방지)
        StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                .append("?serviceKey=").append(koreaTourismApiKey)
                .append("&pageNo=").append(DEFAULT_PAGE_NO)
                .append("&numOfRows=").append(DEFAULT_NUM_OF_ROWS)
                .append("&MobileOS=").append(MOBILE_OS)
                .append("&MobileApp=").append(MOBILE_APP)
                .append("&_type=").append(RESPONSE_TYPE)
                .append("&baseYm=").append(baseYm)
                .append("&areaCd=").append(tourApiAreaCode.getAreaCode())
                .append("&signguCd=").append(tourApiAreaCode.getSigunguCode());

        URI uri = URI.create(uriString.toString());
        KoreaTourismRelatedSpotResponse response = restClient.get()
                .uri(uri)
                .retrieve()
                .body(KoreaTourismRelatedSpotResponse.class);

        if (response == null || !response.isSuccess()) {
            return List.of();
        }

        return response.getRelatedSpots();
    }

    private String getPreviousYearMonth() {
        return YearMonth.now().minusMonths(1).format(BASE_YM_FORMATTER);
    }
}
