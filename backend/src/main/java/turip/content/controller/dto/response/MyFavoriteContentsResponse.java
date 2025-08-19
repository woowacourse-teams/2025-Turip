package turip.content.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import java.util.List;
import turip.content.domain.ContentPlace;
import turip.place.controller.dto.response.PlaceResponse;

public record MyFavoriteContentsResponse(
        List<ContentWithTripInfoAndFavoriteResponse> contents,
        boolean loadable
) {

    public static MyFavoriteContentsResponse of(List<ContentWithTripInfoAndFavoriteResponse> contents,
                                                boolean loadable) {
        return new MyFavoriteContentsResponse(contents, loadable);
    }

    public static record ContentPlaceDetailResponse(
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

    public static record ContentPlaceResponse(
            Long id,
            int visitDay,
            int visitOrder,
            PlaceResponse place,
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "mm:ss")
            LocalTime timeLine,
            boolean isFavoritePlace
    ) {

        public static ContentPlaceResponse of(ContentPlace contentPlace, boolean isFavoritePlace) {
            return new ContentPlaceResponse(
                    contentPlace.getId(),
                    contentPlace.getVisitDay(),
                    contentPlace.getVisitOrder(),
                    PlaceResponse.from(contentPlace.getPlace()),
                    contentPlace.getTimeLine(),
                    isFavoritePlace
            );
        }
    }

    public static record TripDurationResponse(
            int nights,
            int days
    ) {
        public static TripDurationResponse of(int nights, int days) {
            return new TripDurationResponse(nights, days);
        }
    }
}
