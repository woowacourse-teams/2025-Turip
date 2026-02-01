package turip.place.repository;

import jakarta.persistence.QueryHint;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import turip.place.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);

    @QueryHints(@QueryHint(
            name = org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE,
            value = "" + Integer.MIN_VALUE
    ))
    @Query("select c from Category c")
    Stream<Category> streamAll();
}
