package turip.place.repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.place.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    Optional<Place> findByUrl(String url);

    List<Place> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = """
            SELECT p.*
            FROM place p
            WHERE MATCH(p.name) AGAINST(:keyword IN BOOLEAN MODE)
            ORDER BY p.id DESC
            """, nativeQuery = true)
    List<Place> findByNameContaining(@Param("keyword") String keyword, Pageable pageable);

    default String createBooleanModeKeyword(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return "";
        }
        String[] words = keyword.trim().split("\\s+");
        return String.join(" ",
                Arrays.stream(words)
                        .map(word -> word.replaceAll("[+\\-*~<>()\"@]", ""))
                        .map(word -> "+" + word)
                        .filter(word -> word.length() > 1)
                        .toList()
        );
    }
}
