package turip.account.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.container.TestContainerConfig;
import turip.favorite.domain.AccountRole;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@Import(TestContainerConfig.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GuestApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.update("TRUNCATE TABLE refresh_token");
        jdbcTemplate.update("TRUNCATE TABLE favorite_content");
        jdbcTemplate.update("TRUNCATE TABLE favorite_place");
        jdbcTemplate.update("TRUNCATE TABLE favorite_folder");
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
    @DisplayName("/api/v1/guests/migration/availability GET 마이그레이션 가능 여부 조회 테스트")
    class ReadMigrationAvailability {

        @Test
        @DisplayName("마이그레이션 가능 여부 조회 시 200 ok를 응답한다")
        void readMigrationAvailability1() {
            //given
            Long accountId = testDataHelper.insertAccount();
            String deviceFid = "guest";
            jdbcTemplate.update("INSERT INTO guest (id, account_id, device_fid) VALUES (1, ?, ?)", accountId,
                    deviceFid);

            Long folderId1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long folderId2 = testDataHelper.insertFavoriteFolder("커스텀 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId2, AccountRole.OWNER);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .when().get("/api/v1/guests/migration/availability")
                    .then().log().all()
                    .statusCode(200)
                    .body("availability", is(true));
        }
    }

    @Nested
    @DisplayName("/api/v1/guests/me DELETE 게스트 탈퇴 테스트")
    class DeleteGuestTest {

        @Test
        @DisplayName("게스트 탈퇴 시 Guest와 연관된 찜 데이터를 모두 삭제하고 204 No Content를 응답한다")
        void deleteGuestSuccess() {
            // given
            Long accountId = testDataHelper.insertAccount();
            String deviceFid = "guest";
            jdbcTemplate.update("INSERT INTO guest (id, account_id, device_fid) VALUES (1, ?, ?)", accountId,
                    deviceFid);

            Long folderId1 = testDataHelper.insertFavoriteFolder("기본 폴더", true, false);
            Long folderId2 = testDataHelper.insertFavoriteFolder("커스텀 폴더");
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId1, AccountRole.OWNER);
            testDataHelper.insertFavoriteFolderAccount(accountId, folderId2, AccountRole.OWNER);

            jdbcTemplate.update(
                    "INSERT INTO creator (id, profile_image, channel_name) VALUES (1, 'https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update(
                    "INSERT INTO country (id, name, image_url) VALUES (1, '대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (id, name, country_id, province_id, image_url) VALUES (1, '서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (id, creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");

            jdbcTemplate.update(
                    "INSERT INTO favorite_content (id, account_id, content_id, created_at) VALUES (1, 1, 1, '2024-01-01')");

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .when().delete("/api/v1/guests/me")
                    .then().log().all()
                    .statusCode(204);

            // 검증: Guest가 삭제되었는지 확인
            Integer guestCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM guest WHERE id = 1", Integer.class);
            assertThat(guestCount).isEqualTo(0);

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
        @DisplayName("device-fid 헤더 없이 요청 시 400 Bad Request를 응답한다")
        void deleteGuestWithoutDeviceFidHeader() {
            // when & then
            RestAssured
                    .given().log().all()
                    .when().delete("/api/v1/guests/me")
                    .then().log().all()
                    .statusCode(400);
        }
    }
}
