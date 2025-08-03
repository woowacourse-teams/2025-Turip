package turip.favorite.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.favorite.domain.Favorite;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByMemberIdAndContentId(Long memberId, Long contentId);
}
