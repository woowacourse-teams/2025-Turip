package turip.infrastructure.client;

import java.net.URI;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.common.configuration.CacheConfiguration;
import turip.infrastructure.client.dto.KoreaTourismResponse;
import turip.region.domain.LegalDistrictCode;

// TODO: 로그 추가하기
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
        String legalDistrictCode = LegalDistrictCode.getCodeByCityName(cityName);
        if (legalDistrictCode == null) {
            return Optional.empty();
        }

        StringBuilder uriString = new StringBuilder(koreaTourismApiUrl)
                .append("?serviceKey=").append(koreaTourismApiKey)
                .append("&MobileOS=AND")
                .append("&MobileApp=Turip")
                .append("&lDongRegnCd=").append(legalDistrictCode)
                .append("&_type=json")
                .append("&numOfRows=1");

        KoreaTourismResponse response = restClient.get()
                .uri(URI.create(uriString.toString()))
                .retrieve()
                .body(KoreaTourismResponse.class);

        if (response == null) {
            return Optional.empty();
        }
        return response.getFirstThumbImageUrl();
    }
}
