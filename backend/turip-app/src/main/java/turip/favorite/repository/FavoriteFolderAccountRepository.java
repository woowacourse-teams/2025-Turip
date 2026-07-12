package turip.favorite.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.favorite.domain.AccountRole;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.FavoriteFolderAccount;
import turip.favorite.repository.dto.FavoriteFolderItemCountResult;

public interface FavoriteFolderAccountRepository extends JpaRepository<FavoriteFolderAccount, Long> {

    List<FavoriteFolderAccount> findByAccount(Account account);

    int countByFavoriteFolder(FavoriteFolder favoriteFolder);

    @Query("""
            SELECT new turip.favorite.repository.dto.FavoriteFolderItemCountResult(ff.id, COUNT(ffa))
            FROM FavoriteFolder ff
            LEFT JOIN FavoriteFolderAccount ffa ON ffa.favoriteFolder = ff
            WHERE ff.id IN :folderIds
            GROUP BY ff.id
            """)
    List<FavoriteFolderItemCountResult> countByFavoriteFolderIdsIn(List<Long> folderIds);

    Optional<FavoriteFolderAccount> findByFavoriteFolderAndAccount(FavoriteFolder favoriteFolder, Account account);

    @Query("SELECT m FROM Member m " +
            "JOIN FETCH m.account a " +
            "JOIN FavoriteFolderAccount ffa ON ffa.account.id = a.id " +
            "WHERE ffa.favoriteFolder.id = :favoriteFolderId")
    List<Member> findMembersByFavoriteFolderId(@Param("favoriteFolderId") Long favoriteFolderId);

    @Query("SELECT a.id FROM Account a " +
            "WHERE a.id IN (SELECT ffa.account.id FROM FavoriteFolderAccount ffa " +
            "WHERE ffa.favoriteFolder.id = :favoriteFolderId)")
    List<Long> findAccountIdsByFavoriteFolderId(Long favoriteFolderId);

    @Modifying
    @Query("UPDATE FavoriteFolderAccount ffa " +
            "SET ffa.account = :newAccount " +
            "WHERE ffa.account = :oldAccount")
    void updateAccount(@Param("oldAccount") Account oldAccount, @Param("newAccount") Account newAccount);

    boolean existsByFavoriteFolderAndAccountAndAccountRole(FavoriteFolder favoriteFolder, Account account,
                                                           AccountRole accountRole);

    boolean existsByFavoriteFolderAndAccount(FavoriteFolder favoriteFolder, Account account);

    boolean existsByFavoriteFolderIdAndAccount(Long favoriteFolderId, Account account);
}
