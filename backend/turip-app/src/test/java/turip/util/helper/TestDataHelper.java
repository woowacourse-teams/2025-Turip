package turip.util.helper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import turip.account.domain.Account;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.account.domain.TuripMember;
import turip.auth.token.JwtProvider;
import turip.favorite.domain.AccountRole;
import turip.favorite.token.InvitationTokenProvider;

@Component
public class TestDataHelper {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private InvitationTokenProvider invitationTokenProvider;
    
    public void cleanDatabase() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder_account");
        jdbcTemplate.update("DELETE FROM article_place");
        jdbcTemplate.update("DELETE FROM article_tag");

        jdbcTemplate.update("DELETE FROM content_pending");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM turip_member");
        jdbcTemplate.update("DELETE FROM social_member");
        jdbcTemplate.update("DELETE FROM guest");
        jdbcTemplate.update("DELETE FROM refresh_token");
        jdbcTemplate.update("DELETE FROM fcm_token");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM article");
        jdbcTemplate.update("DELETE FROM tag");

        jdbcTemplate.update("DELETE FROM account");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM province");
        jdbcTemplate.update("DELETE FROM country");

        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder_account ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE article_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE article_tag ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content_pending ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE turip_member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE social_member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE guest ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE refresh_token ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE fcm_token ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE article ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE tag ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE account ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
    }

    public String createAccessToken(Long accountId) {
        return jwtProvider.generateAccessToken(accountId, Role.USER);
    }

    public String createAccessToken(Long accountId, Role role) {
        return jwtProvider.generateAccessToken(accountId, role);
    }

    public String createInvitationToken(Long accountId, Long folderId) {
        return invitationTokenProvider.generateToken(accountId, folderId);
    }

    public Long insertAccount() {
        return insertAccount(Role.USER);
    }

    public Long insertAccount(Role role) {
        String nickname = UUID.randomUUID().toString().substring(0, 8);
        return insertAccount(new Account(role, nickname));
    }

    public Long insertAccount(Account account) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("INSERT INTO account (role, nickname) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, account.getRole().name());
            ps.setString(2, account.getNickname());
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }


    public Long insertMember(Long accountId, String email, boolean isFirstLogin) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO member (account_id, email, is_first_login, is_migration_decided) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, accountId);
            ps.setString(2, email);
            ps.setBoolean(3, isFirstLogin);
            ps.setBoolean(4, false);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertFcmToken(Long accountId, String deviceFid, String token, boolean notificationEnabled) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO fcm_token "
                            + "(account_id, device_fid, token, notification_enabled, created_at, updated_at) "
                            + "VALUES (?, ?, ?, ?, NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, accountId);
            ps.setString(2, deviceFid);
            ps.setString(3, token);
            ps.setBoolean(4, notificationEnabled);
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

    public Long insertCountry(String name, String imageUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO country (name, image_url) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setString(2, imageUrl);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertProvince(String name) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO province (name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertCity(String name, Long countryId, Long provinceId, String imageUrl) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setLong(2, countryId);
            if (provinceId != null) {
                ps.setLong(3, provinceId);
            } else {
                ps.setNull(3, java.sql.Types.BIGINT);
            }
            ps.setString(4, imageUrl);
            return ps;
        }, keyHolder);

        return extractGeneratedKey(keyHolder);
    }

    public Long insertCity(String name) {
        Long countryId = insertCountry("대한민국", "");
        Long provinceId = insertProvince(name);
        return insertCity(name, countryId, provinceId, "");
    }

    public Long insertContentPending(Long collectorAccountId, String contentDataJson) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    "INSERT INTO content_pending (collector_account_id, content_data, status, content_data_updated_at, created_at, updated_at) VALUES (?, ?, 'PENDING', NOW(), NOW(), NOW())",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, collectorAccountId);
            ps.setString(2, contentDataJson);
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
