package turip.tripcourse.controller.dto.response;

import java.util.List;
import turip.tripcourse.domain.TripCourse;

public record TripCourseDetailResponse(
        TripDurationResponse tripDuration,
        int tripPlaceCount,
        List<TripCourseResponse> tripCourses
) {
    public static TripCourseDetailResponse from(
            int nights,
            int days,
            int tripPlaceCount,
            List<TripCourse> tripCourses
    ) {
        return new TripCourseDetailResponse(
                TripDurationResponse.from(nights, days),
                tripPlaceCount,
                tripCourses.stream()
                        .map(tripCourse -> TripCourseResponse.from(tripCourse, tripCourse.getPlace()))
                        .toList()
        );
    }
}
