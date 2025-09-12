package turip.content.controller.dto.response.todo;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.content.controller.dto.response.TripDurationResponse;

public record ContentDetailsByRegionCategoryResponse(
        @JsonProperty("content")
        ContentWithCreatorAndCityResponse contentWithCreatorAndCityResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByRegionCategoryResponse of(
            ContentWithCreatorAndCityResponse contentWithCreatorAndCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByRegionCategoryResponse(
                contentWithCreatorAndCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
} 
