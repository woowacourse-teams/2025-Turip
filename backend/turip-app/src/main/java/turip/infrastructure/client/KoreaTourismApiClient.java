package turip.infrastructure.client;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.KoreaTourismResponse;
import turip.region.domain.LegalDistrictCode;

@Component
public class KoreaTourismApiClient {

    private final RestClient restClient;
    private final String koreaTourismApiKey;

    public KoreaTourismApiClient(RestClient baseRestClient,
                                 @Value("${korea-tourism.image.api.key}") String koreaTourismApiKey,
                                 @Value("${korea-tourism.api.url}") String koreaTourismApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(koreaTourismApiUrl)
                .build();
        this.koreaTourismApiKey = koreaTourismApiKey;
    }

    public Optional<String> searchRegionImage(String cityName) {
        String legalDistrictCode = LegalDistrictCode.getCodeByCityName(cityName);
        if (legalDistrictCode == null) {
            return Optional.empty();
        }

        KoreaTourismResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/phokoAwrdList")
                        .queryParam("serviceKey", koreaTourismApiKey)
                        .queryParam("MobileOS", "ETC")
                        .queryParam("MobileApp", "Turip")
                        .queryParam("lDongRegnCd", legalDistrictCode)
                        .queryParam("_type", "json")
                        .queryParam("numOfRows", "1")
                        .build())
                .retrieve()
                .body(KoreaTourismResponse.class);

        if (response == null) {
            return Optional.empty();
        }
        return response.getFirstThumbImageUrl();
    }
}
