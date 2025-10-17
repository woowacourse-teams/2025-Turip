package turip.favorite.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
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

    @EntityGraph(attributePaths = {"place"}, type = EntityGraph.EntityGraphType.FETCH)
    List<FavoritePlace> findAllByFavoriteFolderOrderByFavoriteOrderAsc(FavoriteFolder favoriteFolder);

    @Query("select max(fp.favoriteOrder) from FavoritePlace fp where fp.favoriteFolder = :favoriteFolder")
    Optional<Integer> findMaxFavoriteOrderByFavoriteFolder(@Param("favoriteFolder") FavoriteFolder favoriteFolder);

    @Query("SELECT fp.place.id FROM FavoritePlace fp WHERE fp.favoriteFolder.member= :member AND fp.place IN :places")
    Set<Long> findFavoritedPlaceIdsByFavoriteFolderMemberAndPlaceIn(@Param("member") Member member,
                                                                    @Param("places") List<Place> places);

    @Query("SELECT fp.favoriteFolder.id FROM FavoritePlace fp WHERE fp.place = :place AND fp.favoriteFolder IN :favoriteFolders")
    Set<Long> findFavoriteFolderIdsByPlaceAndFavoriteFolderIn(@Param("place") Place place,
                                                              @Param("favoriteFolders") List<FavoriteFolder> favoriteFolders);
}
