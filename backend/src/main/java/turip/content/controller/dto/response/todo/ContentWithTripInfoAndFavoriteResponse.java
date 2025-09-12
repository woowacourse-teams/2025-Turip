package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentWithTripInfoAndFavoriteResponse(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentWithTripInfoAndFavoriteResponse of(
            ContentResponse ContentResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentWithTripInfoAndFavoriteResponse(
                ContentResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
