package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentDetailsByCityResponse(
        @JsonProperty("content")
        ContentWithoutCityResponse contentWithoutCityResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByCityResponse of(
            ContentWithoutCityResponse contentWithoutCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByCityResponse(
                contentWithoutCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
