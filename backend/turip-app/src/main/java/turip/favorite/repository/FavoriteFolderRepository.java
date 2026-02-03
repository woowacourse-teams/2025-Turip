package turip.favorite.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Account;
import turip.favorite.domain.FavoriteFolder;

public interface FavoriteFolderRepository extends JpaRepository<FavoriteFolder, Long> {

    // TODO: account 관련 수정하기
    List<FavoriteFolder> findAllByAccountOrderByIdAsc(Account account);

    List<FavoriteFolder> findAllByAccount(Account account);

    boolean existsByAccountAndIsDefault(Account account, boolean isDefault);
}
