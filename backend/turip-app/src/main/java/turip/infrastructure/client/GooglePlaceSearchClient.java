package turip.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.GooglePlaceSearchResponse;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchType;

@Component
public class GooglePlaceSearchClient implements PlaceSearchClient {

    private final RestClient restClient;
    private final String googleApiKey;

    public GooglePlaceSearchClient(RestClient baseRestClient,
                                   @Value("${google.api.key}") String googleApiKey,
                                   @Value("${google.api.url}") String googleApiUrl) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(googleApiUrl)
                .build();
        this.googleApiKey = googleApiKey;
    }

    @Override
    public PlaceSearchResponse search(String query) {
        GooglePlaceSearchResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("query", query)
                        .queryParam("key", googleApiKey)
                        .queryParam("language", "ko")
                        .build())
                .retrieve()
                .body(GooglePlaceSearchResponse.class);

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
