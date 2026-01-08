package turip.util.helper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import turip.account.domain.Provider;
import turip.account.domain.Role;

@Component
public class TestDataHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertAccount() {
        return insertAccount(Role.USER);
    }

    public Long insertAccount(Role role) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO account (role) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name());
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Long insertMember(Long accountId, String email, boolean isFirstLogin) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO member (account_id, email, is_first_login) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, accountId);
            ps.setString(2, email);
            ps.setBoolean(3, isFirstLogin);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Long insertSocialMember(Long memberId, Provider provider, String providerId) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO social_member (member_id, provider, provider_id) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, memberId);
            ps.setString(2, provider.name());
            ps.setString(3, providerId);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Long insertSocialMember(String email, boolean isFirstLogin, Provider provider, String providerId) {
        Long accountId = insertAccount();
        Long memberId = insertMember(accountId, email, isFirstLogin);
        return insertSocialMember(memberId, provider, providerId);
    }

    public Long insertTuripMember(Long memberId, String loginId, String loginPassword) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO turip_member (member_id, login_id, login_password) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, memberId);
            ps.setString(2, loginId);
            ps.setString(3, loginPassword);
            return ps;
        }, keyHolder);

        return keyHolder.getKey().longValue();
    }

    public Long insertTuripMember(String email, final boolean isFirstLogin, String loginId, String loginPassword) {
        Long accountId = insertAccount();
        Long memberId = insertMember(accountId, email, isFirstLogin);
        return insertTuripMember(memberId, loginId, loginPassword);
    }
}
