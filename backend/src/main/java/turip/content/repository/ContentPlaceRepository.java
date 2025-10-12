package turip.content.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.content.domain.Content;
import turip.content.domain.ContentPlace;
import turip.place.domain.Place;

public interface ContentPlaceRepository extends JpaRepository<ContentPlace, Long> {

    @EntityGraph(attributePaths = {"place"}, type = EntityGraph.EntityGraphType.FETCH)
    List<ContentPlace> findAllByContentId(Long contentId);

    int countByContentId(Long contentId);

    Optional<ContentPlace> findByContentAndPlaceAndVisitDayAndVisitOrder(Content content, Place place, int visitDay,
                                                                         int visitOrder);
}
