package turip.infrastructure.client;

import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchType;

public interface PlaceSearchClient {
    PlaceSearchResponse search(String query);

    boolean supports(PlaceSearchType type);
}
