package turip.account.api;

import static org.assertj.core.api.Assertions.assertThat;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.auth.token.GoogleTokenParser;
import turip.container.TestContainerConfig;
import turip.favorite.domain.AccountRole;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test-mysql")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberApiTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoBean
    private GoogleTokenParser googleTokenParser;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.update("TRUNCATE TABLE refresh_token");
        jdbcTemplate.update("TRUNCATE TABLE favorite_content");
        jdbcTemplate.update("TRUNCATE TABLE favorite_place");
        jdbcTemplate.update("TRUNCATE TABLE favorite_folder_account");
        jdbcTemplate.update("TRUNCATE TABLE favorite_folder");
        jdbcTemplate.update("TRUNCATE TABLE social_member");
        jdbcTemplate.update("TRUNCATE TABLE member");
        jdbcTemplate.update("TRUNCATE TABLE guest");
        jdbcTemplate.update("TRUNCATE TABLE account");
        jdbcTemplate.update("TRUNCATE TABLE content");
        jdbcTemplate.update("TRUNCATE TABLE creator");
        jdbcTemplate.update("TRUNCATE TABLE city");
        jdbcTemplate.update("TRUNCATE TABLE country");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    @Nested
    @DisplayName("/api/v1/members/migration POST 마이그레이션 테스트")
    class MigrationTest {

        @Test
        @DisplayName("Guest의 데이터를 Member로 마이그레이션하고 204 No Content를 응답한다")
        void migrationSuccess() {
            // given
            // 1. Guest account와 Member account 생성
            Long guestAccountId = testDataHelper.insertAccount();// Guest account
            Long memberAccountId = testDataHelper.insertAccount(); // Member account

            // 2. Guest와 Member 생성
            String guestDeviceFid = "guest-device-123";
            jdbcTemplate.update(
                    "INSERT INTO guest (account_id, device_fid) VALUES (?, ?)",
                    guestAccountId,
                    guestDeviceFid
            );
            Long memberId = testDataHelper.insertMember(memberAccountId, "migration@gmail.com", true);
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-migration";
            testDataHelper.insertSocialMember(memberId, provider, providerId);

            // 3. Guest의 FavoriteFolder와 FavoriteContent 생성
            Long folderId0 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long folderId1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long folderId2 = testDataHelper.insertFavoriteFolder("커스텀 폴더");
            testDataHelper.insertFavoriteFolderAccount(memberAccountId, folderId0, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(guestAccountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(guestAccountId, folderId2, AccountRole.OWNER);

            // FavoriteContent를 위한 Content 생성
            jdbcTemplate.update(
                    "INSERT INTO creator (id, profile_image, channel_name) VALUES (1, 'https://image.example.com/creator1.jpg', 'TravelMate')"
            );
            jdbcTemplate.update(
                    "INSERT INTO country (id, name, image_url) VALUES (1, '대한민국', 'https://image.example.com/korea.jpg')"
            );
            jdbcTemplate.update(
                    "INSERT INTO city (id, name, country_id, province_id, image_url) VALUES (1, '서울', 1, null, 'https://image.example.com/seoul.jpg')"
            );
            jdbcTemplate.update(
                    "INSERT INTO content (id, creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')"
            );

            jdbcTemplate.update(
                    "INSERT INTO favorite_content (id, account_id, content_id, created_at) VALUES (1, ?, 1, '2024-01-01')"
                    , guestAccountId
            );

            String accessToken = testDataHelper.createAccessToken(memberAccountId);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", guestDeviceFid)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/members/migration")
                    .then().log().all()
                    .statusCode(204);

            // 검증: Guest의 FavoriteContent와 FavoriteFolder가 Member의 account_id로 변경되었는지 확인
            Integer favoriteContentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM favorite_content WHERE account_id = 2",
                    Integer.class
            );
            assertThat(favoriteContentCount).isEqualTo(1);

            // 검증: 게스트에 존재하던 폴더가 모두 제거됐는지 확인
            Integer favoriteFolderAccountCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM favorite_folder_account WHERE account_id = ?",
                    Integer.class,
                    guestAccountId
            );
            assertThat(favoriteFolderAccountCount).isEqualTo(0);

            // 검증: 기존에 member에 존재하던 폴더는 제거되고, 마이그레이션 된 폴더만 존재하는지 확인
            Integer migratedFolderCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM favorite_folder_account WHERE account_id = ?",
                    Integer.class,
                    memberAccountId);
            assertThat(migratedFolderCount).isEqualTo(2);

            // 검증: Guest가 삭제되었는지 확인
            Integer guestCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM guest WHERE id = 1",
                    Integer.class
            );
            assertThat(guestCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Authorization 헤더 없이 요청 시 401 Unauthorized를 응답한다")
        void migrationWithoutAuthorizationHeader() {
            // given
            String deviceFid = "device-123";

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .when().post("/api/v1/members/migration")
                    .then().log().all()
                    .statusCode(401);
        }

        @Test
        @DisplayName("device-fid 헤더 없이 요청 시 400 Bad Request를 응답한다")
        void migrationWithoutDeviceFidHeader() {
            // given
            // Member 생성 및 로그인
            String email = "nodevice@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-no-device";

            Long socialMemberId = testDataHelper.insertSocialMember(email, true, provider, providerId);
            Long accountId = jdbcTemplate.queryForObject(
                    "SELECT account_id FROM member WHERE id = (SELECT member_id FROM social_member WHERE id = ?)",
                    Long.class, socialMemberId);

            String accessToken = testDataHelper.createAccessToken(accountId);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/members/migration")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("/api/v1/members/migration/reject POST 마이그레이션 거절 테스트")
    class RejectMigrateTest {

        @Test
        @DisplayName("마이그레이션을 거절한 뒤 204 No Content를 응답한다")
        void reject1() {
            // given
            Long memberAccountId = testDataHelper.insertAccount(); // Member account
            Long memberId = testDataHelper.insertMember(memberAccountId, "migration@gmail.com", true);
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-migration";
            testDataHelper.insertSocialMember(memberId, provider, providerId);
            String accessToken = testDataHelper.createAccessToken(memberAccountId);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/members/migration/reject")
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        @DisplayName("Authorization 헤더 없이 요청 시 401 Unauthorized를 응답한다")
        void reject2() {
            // given
            String deviceFid = "device-123";

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .when().post("/api/v1/members/migration/reject")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("/api/v1/members/me DELETE 회원 탈퇴 테스트")
    class DeleteMemberTest {

        @Test
        @DisplayName("회원 탈퇴 시 Member와 연관된 찜 데이터를 모두 삭제하고 204 No Content를 응답한다")
        void deleteMemberSuccess() {
            // given
            // 1. Account와 Member 생성
            Long accountId = testDataHelper.insertAccount(Role.USER);

            String email = "delete@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-delete";

            Long memberId = testDataHelper.insertMember(accountId, email, true);
            testDataHelper.insertSocialMember(memberId, provider, providerId);

            // 2. Member의 FavoriteFolder 생성
            Long folderId1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long folderId2 = testDataHelper.insertFavoriteFolder("커스텀 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId2, AccountRole.OWNER);

            // 3. FavoriteContent를 위한 Content 생성
            jdbcTemplate.update(
                    "INSERT INTO creator (id, profile_image, channel_name) VALUES (1, 'https://image.example.com/creator1.jpg', 'TravelMate')"
            );
            jdbcTemplate.update(
                    "INSERT INTO country (id, name, image_url) VALUES (1, '대한민국', 'https://image.example.com/korea.jpg')"
            );
            jdbcTemplate.update(
                    "INSERT INTO city (id, name, country_id, province_id, image_url) VALUES (1, '서울', 1, null, 'https://image.example.com/seoul.jpg')"
            );
            jdbcTemplate.update(
                    "INSERT INTO content (id, creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')"
            );

            // 4. Member의 FavoriteContent 생성
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (id, account_id, content_id, created_at) VALUES (1, 1, 1, '2024-01-01')"
            );

            String accessToken = testDataHelper.createAccessToken(accountId);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().delete("/api/v1/members/me")
                    .then().log().all()
                    .statusCode(204);

            // 검증: Member가 삭제되었는지 확인
            Integer memberCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member WHERE id = 1",
                    Integer.class);
            assertThat(memberCount).isEqualTo(0);

            // 검증: Account가 삭제되었는지 확인
            Integer accountCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM account WHERE id = 1",
                    Integer.class);
            assertThat(accountCount).isEqualTo(0);

            // 검증: FavoriteContent가 삭제되었는지 확인
            Integer favoriteContentCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM favorite_content WHERE account_id = 1", Integer.class);
            assertThat(favoriteContentCount).isEqualTo(0);

            // 검증: FavoriteFolderAccount가 삭제되었는지 확인
            Integer favoriteFolderAccountCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM favorite_folder_account WHERE account_id = 1", Integer.class);
            assertThat(favoriteFolderAccountCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Authorization 헤더 없이 요청 시 401 Unauthorized를 응답한다")
        void deleteMemberWithoutAuthorizationHeader() {
            // when & then
            RestAssured
                    .given().log().all()
                    .when().delete("/api/v1/members/me")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
