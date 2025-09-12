package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentDetailsResponse(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentDetailsResponse of(
            ContentResponse content,
            TripDurationResponse tripDuration,
            int tripPlaceCount
    ) {
        return new ContentDetailsResponse(
                content,
                tripDuration,
                tripPlaceCount
        );
    }
} 
