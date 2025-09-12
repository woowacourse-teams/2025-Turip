package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentDetailResponse(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentDetailResponse of(
            ContentResponse content,
            TripDurationResponse tripDuration,
            int tripPlaceCount
    ) {
        return new ContentDetailResponse(
                content,
                tripDuration,
                tripPlaceCount
        );
    }
} 
