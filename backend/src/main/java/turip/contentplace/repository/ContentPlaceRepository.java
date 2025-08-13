package turip.contentplace.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;
import turip.contentplace.domain.ContentPlace;
import turip.place.domain.Place;

public interface ContentPlaceRepository extends JpaRepository<ContentPlace, Long> {

    List<ContentPlace> findAllByContent_Id(Long contentId);

    int countByContent_Id(Long contentId);

    Optional<ContentPlace> findByContentAndPlaceAndVisitDayAndVisitOrder(Content content, Place place, int visitDay,
                                                                         int visitOrder);
}
