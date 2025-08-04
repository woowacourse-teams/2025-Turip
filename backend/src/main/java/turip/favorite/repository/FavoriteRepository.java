package turip.favorite.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.favorite.domain.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByMemberIdAndContentId(Long memberId, Long contentId);

    Optional<Favorite> findByMemberIdAndContentId(Long memberId, Long contentId);
}
