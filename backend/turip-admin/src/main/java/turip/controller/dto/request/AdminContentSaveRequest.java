package turip.controller.dto.request;

import java.time.LocalDate;
import java.util.List;
import turip.content.domain.ContentPendingData;
import turip.content.domain.ContentPendingData.ContentPlaceData;

public record AdminContentSaveRequest(
        String cityName,
        VideoRequest video,
        TripDurationRequest tripDuration,
        List<ContentPlaceRequest> contentPlaces
) {

    public static AdminContentSaveRequest of(
            String cityName,
            VideoRequest video,
            TripDurationRequest tripDuration,
            List<ContentPlaceRequest> contentPlaces
    ) {
        return new AdminContentSaveRequest(cityName, video, tripDuration, contentPlaces);
    }

    public record VideoRequest(
            String videoId,
            String title,
            String url,
            String channelName,
            String channelImage,
            LocalDate uploadedDate
    ) {

        public static VideoRequest from(ContentPendingData data) {
            return new AdminContentSaveRequest.VideoRequest(
                    data.video().videoId(),
                    data.video().title(),
                    data.video().url(),
                    data.video().channelName(),
                    data.video().channelImage(),
                    data.video().uploadedDate()
            );
        }
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

        public static ContentPlaceRequest from(ContentPlaceData data) {
            return new ContentPlaceRequest(
                    data.visitDay(),
                    data.visitOrder(),
                    data.timeLine(),
                    PlaceRequest.from(data)
            );
        }
    }

    public record PlaceRequest(
            String name,
            String url,
            String address,
            double latitude,
            double longitude,
            String categoryName
    ) {

        public static PlaceRequest from(ContentPlaceData data) {
            return new AdminContentSaveRequest.PlaceRequest(
                    data.place().name(),
                    data.place().url(),
                    data.place().address(),
                    data.place().latitude(),
                    data.place().longitude(),
                    data.place().categoryName()
            );
        }
    }
}
