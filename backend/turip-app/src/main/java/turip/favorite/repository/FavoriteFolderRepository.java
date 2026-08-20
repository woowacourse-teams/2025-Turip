package turip.favorite.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ff FROM FavoriteFolder ff WHERE ff.id = :id")
    Optional<FavoriteFolder> findByIdWithLock(@Param("id") Long favoriteFolderId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ff FROM FavoriteFolder ff WHERE ff.id IN :ids ORDER BY ff.id ASC")
    List<FavoriteFolder> findAllByIdInWithLock(@Param("ids") List<Long> favoriteFolderIds);

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

    @Query("SELECT ff FROM FavoriteFolder ff " +
            "JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff " +
            "WHERE ffa.account = :account " +
            "AND (SELECT COUNT(ffa2) FROM FavoriteFolderAccount ffa2 WHERE ffa2.favoriteFolder = ff) = 1")
    List<FavoriteFolder> findAloneFoldersByAccount(@Param("account") Account account);

    @Query("SELECT ff FROM FavoriteFolder ff " +
            "JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff " +
            "WHERE ffa.account = :account AND ff.isDefault = true")
    Optional<FavoriteFolder> findDefaultFoldersByAccount(@Param("account") Account account);
}
