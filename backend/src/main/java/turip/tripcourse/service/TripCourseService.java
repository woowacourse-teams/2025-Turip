package turip.tripcourse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.tripcourse.domain.TripCourse;
import turip.tripcourse.repository.TripCourseRepository;

@Service
@RequiredArgsConstructor
public class TripCourseService {

    private final TripCourseRepository tripCourseRepository;

    public int countByContentId(Long contentId) {
        return tripCourseRepository.countByContent_Id(contentId);
    }

    public int calculateDurationDays(Long contentId) {
        return tripCourseRepository.findAllByContent_Id(contentId)
                .stream()
                .mapToInt(TripCourse::getVisitDay)
                .max()
                .orElse(0);
    }
}
