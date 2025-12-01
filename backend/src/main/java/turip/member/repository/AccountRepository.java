package turip.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import turip.member.domain.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {


}
