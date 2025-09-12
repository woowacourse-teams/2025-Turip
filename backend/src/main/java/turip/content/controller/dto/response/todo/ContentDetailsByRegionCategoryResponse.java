package turip.content.controller.dto.response.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.content.controller.dto.response.TripDurationResponse;

public record ContentDetailsByRegionCategoryResponse(
        @JsonProperty("content")
        ContentResponse contentResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByRegionCategoryResponse of(
            ContentResponse contentResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByRegionCategoryResponse(
                contentResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
} 
