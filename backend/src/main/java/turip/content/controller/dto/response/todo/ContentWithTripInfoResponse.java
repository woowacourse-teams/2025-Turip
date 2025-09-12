package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentWithTripInfoResponse(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentWithTripInfoResponse of(
            ContentResponse contentResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentWithTripInfoResponse(
                contentResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
