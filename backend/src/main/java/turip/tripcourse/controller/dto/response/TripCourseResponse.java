package turip.tripcourse.controller.dto.response;

import turip.place.controller.dto.response.PlaceResponse;
import turip.place.domain.Place;
import turip.tripcourse.domain.TripCourse;

public record TripCourseResponse(
        Long id,
        int visitDay,
        int visitOrder,
        PlaceResponse place
) {
    public static TripCourseResponse from(TripCourse tripCourse, Place place) {
        return new TripCourseResponse(
                tripCourse.getId(),
                tripCourse.getVisitDay(),
                tripCourse.getVisitOrder(),
                PlaceResponse.from(place)
        );
    }
}
