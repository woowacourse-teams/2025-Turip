package turip.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@Getter
@NoArgsConstructor
public class GooglePlaceSearchResponse {

    public static final String MAP_URL_PREFIX = "https://www.google.com/maps/search/?api=1&query=Google&query_place_id=";

    @JsonProperty("results")
    private List<Result> results;

    public PlaceSearchResponse toPlaceSearchResponse() {
        if (results == null) {
            return new PlaceSearchResponse(PlaceSearchProvider.GOOGLE, Collections.emptyList());
        }
        List<PlaceSearchResponse.PlaceSearchItem> items = results.stream()
                .map(result -> new PlaceSearchResponse.PlaceSearchItem(
                        result.placeId,
                        result.name,
                        MAP_URL_PREFIX + result.placeId,
                        result.formattedAddress,
                        result.geometry.location.lat,
                        result.geometry.location.lng,
                        result.types != null && !result.types.isEmpty() ? result.types.getFirst() : null))
                .toList();
        return new PlaceSearchResponse(PlaceSearchProvider.GOOGLE, items);
    }

    @Getter
    @NoArgsConstructor
    public static class Result {
        @JsonProperty("place_id")
        private String placeId;
        private String name;
        @JsonProperty("formatted_address")
        private String formattedAddress;
        private String website;
        private List<String> types;
        private Geometry geometry;
    }

    @Getter
    @NoArgsConstructor
    public static class Geometry {
        private Location location;
    }

    @Getter
    @NoArgsConstructor
    public static class Location {
        private double lat;
        private double lng;
    }
}
