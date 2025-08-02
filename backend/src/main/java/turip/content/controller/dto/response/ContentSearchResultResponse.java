package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentSearchResultResponse(
        @JsonProperty("content")
        ContentWithoutCityResponse contentWithoutCityResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentSearchResultResponse of(
            ContentWithoutCityResponse contentWithoutCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentSearchResultResponse(
                contentWithoutCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
