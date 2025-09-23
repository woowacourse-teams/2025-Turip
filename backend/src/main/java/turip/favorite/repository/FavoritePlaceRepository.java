package turip.favorite.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.member.domain.Member;
import turip.place.domain.Place;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {

    int countByFavoriteFolder(FavoriteFolder favoriteFolder);

    boolean existsByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place);

    Optional<FavoritePlace> findByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place);

    List<FavoritePlace> findAllByFavoriteFolderOrderByFavoriteOrderAsc(FavoriteFolder favoriteFolder);

    @Query("select max(fp.favoriteOrder) from FavoritePlace fp where fp.favoriteFolder = :favoriteFolder")
    Optional<Integer> findMaxFavoriteOrderByFavoriteFolder(@Param("favoriteFolder") FavoriteFolder favoriteFolder);

    boolean existsByFavoriteFolderMemberAndPlace(Member member, Place place);
}
