package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    @Query("SELECT ff FROM FavoriteFolder ff " +
            "JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff " +
            "WHERE ffa.account = :account ")
    List<FavoriteFolder> findAllByAccount(Account account);

    @Query("SELECT ff FROM FavoriteFolder ff " +
            "JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff " +
            "WHERE ffa.account = :account " +
            "ORDER BY ffa.id ASC")
    List<FavoriteFolder> findAllByAccountOrderByFavoriteFolderAccountIdAsc(@Param("account") Account account);

    @Query("SELECT CASE WHEN COUNT(ff) > 0 THEN true ELSE false END FROM FavoriteFolder ff " +
            "JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff " +
            "WHERE ffa.account = :account AND ff.isDefault = false")
    boolean existsCustomFolderByAccount(@Param("account") Account account);

    @Modifying
    @Query("DELETE FROM FavoriteFolder ff " +
            "WHERE ff.id IN (" +
            "   SELECT ffa.favoriteFolder.id " +
            "   FROM FavoriteFolderAccount ffa " +
            "   WHERE ffa.account = :account " +
            ") " +
            "AND ff.isShared = false")
    void deletePersonalFoldersByAccount(@Param("account") Account account);
}
