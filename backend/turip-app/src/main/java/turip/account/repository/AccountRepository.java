package turip.account.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.account.domain.FcmToken;
import turip.account.domain.Role;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByNickname(String nickname);

    List<Account> findAllByRole(Role role);

    @Query("SELECT f FROM FcmToken f WHERE f.account.id IN :accountIds AND f.notificationEnabled = true")
    List<FcmToken> findFcmTokensByAccountIds(@Param("accountIds") List<Long> accountIds);
}
