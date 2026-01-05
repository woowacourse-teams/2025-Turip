package turip.util.helper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import turip.account.domain.Role;

@Component
public class TestDataHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertAccount() {
        return insertAccount(Role.USER);
    }

    public Long insertAccount(Role role) {
        jdbcTemplate.update("INSERT INTO account (role) VALUES (?)", role.name());

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO account (role) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }
}
