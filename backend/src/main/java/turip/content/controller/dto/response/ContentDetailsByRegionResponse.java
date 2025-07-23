package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentDetailsByRegionResponse(
        @JsonProperty("content")
        ContentWithoutRegionResponse contentWithoutRegionResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByRegionResponse of(
            ContentWithoutRegionResponse contentWithoutRegionResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByRegionResponse(
                contentWithoutRegionResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
