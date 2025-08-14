package turip.contentplace.controller.dto.response;

import java.util.List;
import turip.contentplace.domain.ContentPlace;

public record ContentPlaceDetailResponse(
        TripDurationResponse tripDuration,
        int contentPlaceCount,
        List<ContentPlaceResponse> contentPlaces
) {
    
    public static ContentPlaceDetailResponse of(
            int nights,
            int days,
            int contentPlaceCount,
            List<ContentPlace> contentPlaces
    ) {
        return new ContentPlaceDetailResponse(
                TripDurationResponse.of(nights, days),
                contentPlaceCount,
                contentPlaces.stream()
                        .map(contentPlace -> ContentPlaceResponse.of(contentPlace, contentPlace.getPlace()))
                        .toList()
        );
    }
}
