package turip.favorite.repository;

import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ff FROM FavoriteFolder ff WHERE ff.id = :id")
    Optional<FavoriteFolder> findByIdWithLock(@Param("id") Long favoriteFolderId);

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
    @Query(value = "DELETE ff FROM favorite_folder ff " +
            "JOIN favorite_folder_account ffa ON ffa.favorite_folder_id = ff.id " +
            "WHERE ffa.account_id = :#{#account.id} " +
            "AND ff.is_shared = false",
            nativeQuery = true)
    void deletePersonalFoldersByAccount(@Param("account") Account account);

    @Modifying
    @Query(value = "DELETE ff FROM favorite_folder ff " +
            "JOIN favorite_folder_account ffa ON ffa.favorite_folder_id = ff.id " +
            "WHERE ffa.account_id = :#{#account.id} " +
            "AND ff.is_default = true",
            nativeQuery = true)
    void deleteDefaultFolderByAccount(@Param("account") Account account);
}
