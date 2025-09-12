package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentWithTripInfo(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentWithTripInfo of(
            ContentResponse ContentResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentWithTripInfo(
                ContentResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
