package turip.contentplace.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalTime;
import turip.contentplace.domain.ContentPlace;
import turip.place.controller.dto.response.PlaceResponse;
import turip.place.domain.Place;

public record ContentPlaceResponse(
        Long id,
        int visitDay,
        int visitOrder,
        PlaceResponse place,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "mm:ss")
        LocalTime timeLine
) {
    public static ContentPlaceResponse of(ContentPlace contentPlace, Place place) {
        return new ContentPlaceResponse(
                contentPlace.getId(),
                contentPlace.getVisitDay(),
                contentPlace.getVisitOrder(),
                PlaceResponse.from(place),
                contentPlace.getTimeLine()
        );
    }
}
