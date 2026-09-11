package turip.infrastructure.client;

import java.net.URI;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import turip.common.configuration.CacheConfiguration;
import turip.infrastructure.client.dto.KoreaTourismImageResponse;
import turip.region.domain.TourApiLegalDongCode;

@Slf4j
@Component
public class KoreaTourismImageClient {

    private final RestClient restClient;
    private final String koreaTourismApiKey;
    private final String koreaTourismApiUrl;

    public KoreaTourismImageClient(RestClient baseRestClient,
                                   @Value("${korea-tourism.api.key}") String koreaTourismApiKey,
                                   @Value("${korea-tourism.api.image-url}") String koreaTourismApiUrl) {
        this.restClient = baseRestClient;
        this.koreaTourismApiKey = koreaTourismApiKey;
        this.koreaTourismApiUrl = koreaTourismApiUrl;
    }

    @Cacheable(value = CacheConfiguration.KOREA_TOURISM_IMAGE_CACHE, key = "#cityName", unless = "#result == null || #result.isEmpty()")
    public Optional<String> searchRegionImage(String cityName) {
        String legalDistrictCode = TourApiLegalDongCode.getCodeByCityName(cityName);
        if (legalDistrictCode == null) {
            return Optional.empty();
        }

        try {
            StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                    .append("?serviceKey=").append(koreaTourismApiKey)
                    .append("&MobileOS=AND")
                    .append("&MobileApp=Turip")
                    .append("&lDongRegnCd=").append(legalDistrictCode)
                    .append("&_type=json")
                    .append("&numOfRows=1");

            URI uri = URI.create(uriString.toString());
            KoreaTourismImageResponse response = restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(KoreaTourismImageResponse.class);

            if (response == null) {
                log.warn("한국관광공사 이미지 API 응답 파싱 실패: cityName={}", cityName);
                return Optional.empty();
            }

            if (!response.isSuccess()) {
                log.warn("한국관광공사 이미지 API 응답 실패: cityName={}, resultCode={}, resultMsg={}",
                        cityName,
                        response.getResultCode(),
                        response.getResultMsg());
                return Optional.empty();
            }

            return response.getFirstThumbImageUrl();
        } catch (RestClientResponseException e) {
            log.warn("한국관광공사 이미지 API 호출 실패: cityName={}, statusCode={}, message={}",
                    cityName,
                    e.getStatusCode().value(),
                    e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.warn("한국관광공사 이미지 API 호출 실패: cityName={}, message={}",
                    cityName,
                    e.getMessage());
            return Optional.empty();
        }
    }
}
