package turip.controller.dto.request;

import java.time.LocalDate;
import java.util.List;

public record AdminContentSaveRequest(
        String cityName,
        VideoRequest video,
        TripDurationRequest tripDuration,
        List<ContentPlaceRequest> contentPlaces
) {

    public record VideoRequest(
            String videoId,
            String title,
            String url,
            String channelName,
            String channelImage,
            LocalDate uploadedDate
    ) {
    }

    public record TripDurationRequest(
            int nights,
            int days
    ) {
    }

    public record ContentPlaceRequest(
            int visitDay,
            int visitOrder,
            String timeLine,
            PlaceRequest place
    ) {
    }

    public record PlaceRequest(
            String name,
            String url,
            String address,
            double latitude,
            double longitude,
            String categoryName
    ) {
    }
}
