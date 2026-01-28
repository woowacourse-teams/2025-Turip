package turip.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.account.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {


}
