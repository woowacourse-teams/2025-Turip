package turip.tripcourse.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.tripcourse.domain.TripCourse;

public interface TripCourseRepository extends JpaRepository<TripCourse, Long> {

    List<TripCourse> findAllByContent_Id(Long contentID);

    int countByContent_Id(Long contentId);
}
