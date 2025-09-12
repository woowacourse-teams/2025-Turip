package turip.content.controller.dto.response.todo;

import turip.content.controller.dto.response.TripDurationResponse;

public record ContentWithTripInfoResponse(
        ContentWithCreatorAndCityResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentWithTripInfoResponse of(
            ContentWithCreatorAndCityResponse contentWithCreatorAndCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentWithTripInfoResponse(
                contentWithCreatorAndCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
