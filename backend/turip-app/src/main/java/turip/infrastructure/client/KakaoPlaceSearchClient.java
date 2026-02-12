package turip.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.KakaoPlaceSearchResponse;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchType;

@Component
public class KakaoPlaceSearchClient implements PlaceSearchClient {

    private final RestClient restClient;
    private final String kakaoApiKey;

    public KakaoPlaceSearchClient(RestClient baseRestClient,
                                  @Value("${kakao.api.key}") String kakaoApiKey,
                                  @Value("${kakao.api.url}") String kakaoApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(kakaoApiUrl)
                .build();
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
