package turip.favorite.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoritePlace;
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;
import turip.place.domain.Place;

public interface FavoritePlaceRepository extends JpaRepository<FavoritePlace, Long> {

    int countByFavoriteFolder(FavoriteFolder favoriteFolder);

    @Query("""
            SELECT new turip.favorite.repository.dto.FavoriteFolderItemCountResult(ff.id, COUNT(fp))
            FROM FavoriteFolder ff
            LEFT JOIN FavoritePlace fp ON fp.favoriteFolder = ff
            WHERE ff.id IN :folderIds
            GROUP BY ff.id
            """)
    List<FavoriteFolderItemCountResult> countByFavoriteFolderIdsIn(@Param("folderIds") List<Long> folderIds);

    boolean existsByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place);

    Optional<FavoritePlace> findByFavoriteFolderAndPlace(FavoriteFolder favoriteFolder, Place place);

    @EntityGraph(attributePaths = {"place"}, type = EntityGraph.EntityGraphType.FETCH)
    List<FavoritePlace> findAllByFavoriteFolderOrderByFavoriteOrderAsc(FavoriteFolder favoriteFolder);

    @Query("select max(fp.favoriteOrder) from FavoritePlace fp where fp.favoriteFolder = :favoriteFolder")
    Optional<Integer> findMaxFavoriteOrderByFavoriteFolder(@Param("favoriteFolder") FavoriteFolder favoriteFolder);

    @Query("""
            SELECT DISTINCT fp.place.id
            FROM FavoritePlace fp
            JOIN fp.favoriteFolder ff
            JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff
            WHERE ffa.account = :account
            AND fp.place IN :places
            """)
    Set<Long> findFavoritedPlaceIdsByAccountAndPlaceIn(@Param("account") Account account,
                                                       @Param("places") List<Place> places);

    @Query("SELECT fp.favoriteFolder.id FROM FavoritePlace fp WHERE fp.place = :place AND fp.favoriteFolder IN :favoriteFolders")
    Set<Long> findFavoriteFolderIdsByPlaceAndFavoriteFolderIn(@Param("place") Place place,
                                                              @Param("favoriteFolders") List<FavoriteFolder> favoriteFolders);

    @Query("""
            SELECT fp
            FROM FavoritePlace fp
            JOIN fp.favoriteFolder ff
            JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff
            WHERE fp.place = :place
            AND ffa.account = :account
            """)
    List<FavoritePlace> findAllByPlaceAndAccount(@Param("place") Place place, @Param("account") Account account);

    @Query("""
            SELECT COUNT(fp)
            FROM FavoritePlace fp
            JOIN fp.favoriteFolder ff
            JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff
            WHERE ffa.account = :account
            """)
    int countByAccount(@Param("account") Account account);

    @Query("""
            SELECT CASE WHEN COUNT(fp) > 0 THEN true ELSE false END
            FROM FavoritePlace fp
            JOIN fp.favoriteFolder ff
            JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff
            WHERE ffa.account = :account
            """)
    boolean existsByAccount(@Param("account") Account account);

    void deleteAllByFavoriteFolder(FavoriteFolder favoriteFolder);
}
