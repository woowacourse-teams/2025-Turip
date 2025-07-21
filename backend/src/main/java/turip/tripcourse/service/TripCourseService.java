package turip.tripcourse.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.tripcourse.controller.dto.response.TripCourseDetailResponse;
import turip.tripcourse.domain.TripCourse;
import turip.tripcourse.repository.TripCourseRepository;

@Service
@RequiredArgsConstructor
public class TripCourseService {

    private final TripCourseRepository tripCourseRepository;

    public TripCourseDetailResponse findTripCourseDetails(Long contentId) {
        List<TripCourse> tripCourses = tripCourseRepository.findAllByContent_Id(contentId);
        int days = calculateDurationDays(contentId);
        int nights = days - 1;
        return TripCourseDetailResponse.from(nights, days, 0, tripCourses);
    }

    public int calculateDurationDays(Long contentId) {
        return tripCourseRepository.findAllByContent_Id(contentId)
                .stream()
                .mapToInt(TripCourse::getVisitDay)
                .max()
                .orElse(0);
    }

    private int countByContentId(Long contentId) {
        return tripCourseRepository.countByContent_Id(contentId);
    }
}
