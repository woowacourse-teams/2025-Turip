package turip.place.domain;

import turip.place.controller.dto.response.PlaceSearchResponse;

public interface PlaceSearchClient {
    PlaceSearchResponse search(String query);

    boolean supports(PlaceSearchType type);
}
