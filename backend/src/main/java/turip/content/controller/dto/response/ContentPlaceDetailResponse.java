package turip.content.controller.dto.response;

import java.util.List;

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
