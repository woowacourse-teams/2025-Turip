package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentDetailsByRegionCategoryResponse(
        @JsonProperty("content")
        ContentByCityResponse contentByCityResponse,
        @JsonProperty("tripDuration")
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByRegionCategoryResponse of(
            ContentByCityResponse contentByCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByRegionCategoryResponse(
                contentByCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
} 
