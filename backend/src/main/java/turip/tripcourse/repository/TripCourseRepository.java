package turip.tripcourse.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.tripcourse.domain.TripCourse;
import turip.content.domain.Content;
import turip.place.domain.Place;

public interface TripCourseRepository extends JpaRepository<TripCourse, Long> {

    List<TripCourse> findAllByContent_Id(Long contentId);

    int countByContent_Id(Long contentId);
    
    Optional<TripCourse> findByContentAndPlaceAndVisitDayAndVisitOrder(Content content, Place place, int visitDay, int visitOrder);
}
