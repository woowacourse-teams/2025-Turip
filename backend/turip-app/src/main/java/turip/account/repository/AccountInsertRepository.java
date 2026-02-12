package turip.account.repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import turip.account.domain.Account;

@Repository
@RequiredArgsConstructor
public class AccountInsertRepository {

    private final JdbcTemplate jdbcTemplate;
    private final AccountRepository accountRepository;

    public Optional<Account> trySave(Account account) {
        String sql = "INSERT INTO account (nickname, role) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, account.getNickname());
                ps.setString(2, account.getRole().name());
                return ps;
            }, keyHolder);

            return Optional.ofNullable(keyHolder.getKey())
                    .map(Number::longValue)
                    .flatMap(accountRepository::findById);
        } catch (DataIntegrityViolationException e) {
            return Optional.empty();
        }
    }
}
