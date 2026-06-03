package turip.infrastructure.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.common.configuration.CacheConfiguration;
import turip.infrastructure.client.dto.KoreaTourismResponse;
import turip.region.domain.LegalDistrictCode;

@Component
public class KoreaTourismApiClient {

    private final RestClient restClient;
    private final String koreaTourismApiKey;

    public KoreaTourismApiClient(RestClient baseRestClient,
                                 @Value("${korea-tourism.image.api.key}") String koreaTourismApiKey,
                                 @Value("${korea-tourism.image.api.url}") String koreaTourismApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(koreaTourismApiUrl)
                .build();
        this.koreaTourismApiKey = koreaTourismApiKey;
    }

    @Cacheable(value = CacheConfiguration.KOREA_TOURISM_IMAGE_CACHE, key = "#cityName", unless = "#result == null || #result.isEmpty()")
    public Optional<String> searchRegionImage(String cityName) {
        String legalDistrictCode = LegalDistrictCode.getCodeByCityName(cityName);
        if (legalDistrictCode == null) {
            return Optional.empty();
        }

        String encodedServiceKey = URLEncoder.encode(koreaTourismApiKey, StandardCharsets.UTF_8);

        String uriString = String.format(
                "https://apis.data.go.kr/B551011/PhokoAwrdService/phokoAwrdList?serviceKey=%s&MobileOS=AND&MobileApp=Turip&lDongRegnCd=%s&_type=json&numOfRows=1",
                encodedServiceKey,
                legalDistrictCode
        );

        KoreaTourismResponse response = restClient.get()
                .uri(URI.create(uriString))
                .retrieve()
                .body(KoreaTourismResponse.class);

        if (response == null) {
            return Optional.empty();
        }
        return response.getFirstThumbImageUrl();
    }
}
