package turip.content.controller.dto.response.place;

import java.util.List;
import turip.content.controller.dto.response.content.TripDurationResponse;

public record ContentPlaceDetailResponse(
        List<ContentPlaceResponse> contentPlaces,
        int contentPlaceCount,
        TripDurationResponse tripDuration
) {

    public static ContentPlaceDetailResponse of(
            int nights,
            int days,
            int contentPlaceCount,
            List<ContentPlaceResponse> contentPlaces
    ) {
        return new ContentPlaceDetailResponse(
                contentPlaces,
                contentPlaceCount,
                TripDurationResponse.of(nights, days)
        );
    }
}
