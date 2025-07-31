package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentSearchResultResponse(
        @JsonProperty("content")
        ContentWithoutRegionResponse contentWithoutRegionResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentSearchResultResponse of(
            ContentWithoutRegionResponse contentWithoutRegionResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentSearchResultResponse(
                contentWithoutRegionResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
