package turip.place.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchClient;
import turip.place.domain.PlaceSearchType;
import turip.place.infrastructure.client.dto.KakaoPlaceSearchResponse;

@Component
public class KakaoPlaceSearchClient implements PlaceSearchClient {

    private final RestClient restClient;
    private final String kakaoApiKey;

    public KakaoPlaceSearchClient(RestClient.Builder restClientBuilder,
                                  @Value("${kakao.api.key}") String kakaoApiKey,
                                  @Value("${kakao.api.url}") String kakaoApiUrl) {
        this.restClient = restClientBuilder.baseUrl(kakaoApiUrl).build();
        this.kakaoApiKey = kakaoApiKey;
    }

    @Override
    public PlaceSearchResponse search(String query) {
        KakaoPlaceSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .build())
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .body(KakaoPlaceSearchResponse.class);

        if (response == null) {
            return new PlaceSearchResponse(null, null);
        }
        return response.toPlaceSearchResponse();
    }

    @Override
    public boolean supports(PlaceSearchType type) {
        return type == PlaceSearchType.DOMESTIC;
    }
}
