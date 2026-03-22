package turip.content.domain;

import java.time.LocalDate;
import java.util.List;

public record ContentPendingData(
        String cityName,
        VideoData video,
        List<ContentPlaceData> contentPlaces
) {

    public record VideoData(
            String videoId,
            String title,
            String url,
            String channelName,
            String channelImage,
            LocalDate uploadedDate
    ) {
    }

    public record ContentPlaceData(
            int visitDay,
            int visitOrder,
            String timeLine,
            PlaceData place
    ) {
    }

    public record PlaceData(
            String name,
            String url,
            String address,
            double latitude,
            double longitude,
            String categoryName
    ) {
    }
}
