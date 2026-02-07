package turip.util.helper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.favorite.domain.AccountRole;

@Component
public class TestDataHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Long insertAccount() {
        return insertAccount(Role.USER);
    }

    public Long insertAccount(Role role) {
        String nickname = UUID.randomUUID().toString().substring(0, 8);
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO account (role, nickname) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, role.name());
            ps.setString(2, nickname);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
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

        return extractGeneratedKey(keyHolder);
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

        return extractGeneratedKey(keyHolder);
    }

    public Long insertSocialMember(String email, boolean isFirstLogin, Provider provider, String providerId) {
        Long accountId = insertAccount();
        Long memberId = insertMember(accountId, email, isFirstLogin);
        return insertSocialMember(memberId, provider, providerId);
    }

    public Long insertTuripMember(Long memberId, String loginId, String loginPassword) {
        TuripMember turipMember = new TuripMember(null, loginId, loginPassword);
        String encodedPassword = turipMember.getLoginPassword();

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO turip_member (member_id, login_id, login_password) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, memberId);
            ps.setString(2, loginId);
            ps.setString(3, encodedPassword);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertTuripMember(Long accountId, String email, boolean isFirstLogin, String loginId,
                                  String loginPassword) {
        Long memberId = insertMember(accountId, email, isFirstLogin);
        return insertTuripMember(memberId, loginId, loginPassword);
    }

    public Long insertTuripMember(String email, boolean isFirstLogin, String loginId, String loginPassword) {
        Long accountId = insertAccount();
        Long memberId = insertMember(accountId, email, isFirstLogin);
        return insertTuripMember(memberId, loginId, loginPassword);
    }

    public Long insertFavoriteFolder(String name, boolean isDefault, boolean isShared) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO favorite_folder (name, is_default, is_shared) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setBoolean(2, isDefault);
            ps.setBoolean(3, isShared);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertFavoriteFolder(String name) {
        return insertFavoriteFolder(name, false, false);
    }

    public Long insertFavoriteFolderAccount(Long accountId, Long favoriteFolderId, AccountRole accountRole) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO favorite_folder_account (account_id, favorite_folder_id, account_role) VALUES (?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, accountId);
            ps.setLong(2, favoriteFolderId);
            ps.setString(3, accountRole.name());
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    private Long extractGeneratedKey(KeyHolder keyHolder) {
        Number key = keyHolder.getKey();
        if (key == null) {
            throw new IllegalArgumentException("insert 후 반환된 key값이 null 입니다.");
        }
        return key.longValue();
    }
}
