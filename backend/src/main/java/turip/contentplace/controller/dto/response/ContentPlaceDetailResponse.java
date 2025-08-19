package turip.contentplace.controller.dto.response;

import java.util.List;

public record ContentPlaceDetailResponse(
        TripDurationResponse tripDuration,
        int contentPlaceCount,
        List<ContentPlaceResponse> contentPlaces
) {

    public static ContentPlaceDetailResponse of(
            int nights,
            int days,
            int contentPlaceCount,
            List<ContentPlaceResponse> contentPlaces
    ) {
        return new ContentPlaceDetailResponse(
                TripDurationResponse.of(nights, days),
                contentPlaceCount,
                contentPlaces
        );
    }
}
