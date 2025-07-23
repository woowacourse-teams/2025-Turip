package turip.tripcourse.controller.dto.response;

import java.util.List;
import turip.tripcourse.domain.TripCourse;

public record TripCourseDetailResponse(
        TripDurationResponse tripDuration,
        int tripPlaceCount,
        List<TripCourseResponse> tripCourses
) {
    public static TripCourseDetailResponse of(
            int nights,
            int days,
            int tripPlaceCount,
            List<TripCourse> tripCourses
    ) {
        return new TripCourseDetailResponse(
                TripDurationResponse.of(nights, days),
                tripPlaceCount,
                tripCourses.stream()
                        .map(tripCourse -> TripCourseResponse.of(tripCourse, tripCourse.getPlace()))
                        .toList()
        );
    }
}
