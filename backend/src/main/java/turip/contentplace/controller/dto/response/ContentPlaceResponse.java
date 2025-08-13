package turip.contentplace.controller.dto.response;

import turip.contentplace.domain.ContentPlace;
import turip.place.controller.dto.response.PlaceResponse;
import turip.place.domain.Place;

public record ContentPlaceResponse(
        Long id,
        int visitDay,
        int visitOrder,
        PlaceResponse place
) {
    public static ContentPlaceResponse of(ContentPlace contentPlace, Place place) {
        return new ContentPlaceResponse(
                contentPlace.getId(),
                contentPlace.getVisitDay(),
                contentPlace.getVisitOrder(),
                PlaceResponse.from(place)
        );
    }
}
