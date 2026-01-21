package turip.place.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchClient;
import turip.place.domain.PlaceSearchType;
import turip.place.infrastructure.client.dto.KakaoPlaceSearchResponse;

@Slf4j
@Component
public class KakaoPlaceSearchClient implements PlaceSearchClient {

    private final WebClient webClient;
    private final String kakaoApiKey;

    public KakaoPlaceSearchClient(WebClient.Builder webClientBuilder,
                                  @Value("${kakao.api.key}") String kakaoApiKey,
                                  @Value("${kakao.api.url}") String kakaoApiUrl) {
        this.webClient = webClientBuilder.baseUrl(kakaoApiUrl).build();
        this.kakaoApiKey = kakaoApiKey;
    }

    @Override
    public PlaceSearchResponse search(String query) {
        KakaoPlaceSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .build())
                .header("Authorization", "KakaoAK " + kakaoApiKey)
                .retrieve()
                .bodyToMono(KakaoPlaceSearchResponse.class)
                .block();

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
