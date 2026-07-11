package turip.account.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import turip.account.domain.Account;
import turip.account.domain.FcmToken;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByAccountAndDeviceFid(Account account, String deviceFid);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("delete from FcmToken f "
            + "where f.token = :token and (f.account <> :account or f.deviceFid <> :deviceFid)")
    void deleteReassignedToken(@Param("token") String token,
                               @Param("account") Account account,
                               @Param("deviceFid") String deviceFid);

    void deleteByAccountAndDeviceFid(Account account, String deviceFid);

    void deleteByAccount(Account account);
}
