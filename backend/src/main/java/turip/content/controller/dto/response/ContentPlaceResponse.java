package turip.content.controller.dto.response;


import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import turip.content.domain.ContentPlace;
import turip.place.controller.dto.response.PlaceResponse;

public record ContentPlaceResponse(
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