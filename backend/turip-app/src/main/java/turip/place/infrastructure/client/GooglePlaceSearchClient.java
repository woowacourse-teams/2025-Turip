package turip.place.infrastructure.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchClient;
import turip.place.domain.PlaceSearchType;
import turip.place.infrastructure.client.dto.GooglePlaceSearchResponse;

@Slf4j
@Component
public class GooglePlaceSearchClient implements PlaceSearchClient {

    private final WebClient webClient;
    private final String googleApiKey;

    public GooglePlaceSearchClient(WebClient.Builder webClientBuilder,
                                   @Value("${google.api.key}") String googleApiKey,
                                   @Value("${google.api.url}") String googleApiUrl) {
        this.webClient = webClientBuilder.baseUrl(googleApiUrl).build();
        this.googleApiKey = googleApiKey;
    }

    @Override
    public PlaceSearchResponse search(String query) {
        GooglePlaceSearchResponse response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .queryParam("key", googleApiKey)
                        .build())
                .retrieve()
                .bodyToMono(GooglePlaceSearchResponse.class)
                .block();

        if (response == null) {
            return new PlaceSearchResponse(null, null);
        }
        return response.toPlaceSearchResponse();
    }

    @Override
    public boolean supports(PlaceSearchType type) {
        return type == PlaceSearchType.OVERSEAS;
    }
}
