package turip.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Role;
import turip.favorite.domain.FavoriteContent;
import turip.favorite.repository.FavoriteContentRepository;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
class AdminBookmarkApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private FavoriteContentRepository favoriteContentRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("/api/v1/admin/bookmarks GET 콘텐츠별 ADMIN 계정 북마크 상태 조회 테스트")
    class FindAccountBookmarkStatusesTest {

        @Test
        @DisplayName("전체 ADMIN 계정과 각 계정의 지난주 북마크 여부를 응답한다")
        void findAccountBookmarkStatuses1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            Long adminAccountId1 = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId1, "admin1@turip.com", false, "admin1", "password123!");
            Long adminAccountId2 = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId2, "admin2@turip.com", false, "admin2", "password123!");

            jdbcTemplate.update("INSERT INTO favorite_content (created_at, account_id, content_id) VALUES (?, ?, ?)",
                    "2024-01-01", adminAccountId1, 1);

            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId1, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/bookmarks?contentId=1")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].accountId", is(adminAccountId1.intValue()))
                    .body("[0].isBookmarked", is(true))
                    .body("[1].accountId", is(adminAccountId2.intValue()))
                    .body("[1].isBookmarked", is(false));
        }

        @Test
        @DisplayName("존재하지 않는 콘텐츠를 조회하면 404 Not Found를 응답한다")
        void findAccountBookmarkStatuses2() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/bookmarks?contentId=999")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 조회하면 403 Forbidden을 응답한다")
        void findAccountBookmarkStatuses3() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/bookmarks?contentId=1")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/bookmarks POST ADMIN 계정 북마크 등록 테스트")
    class CreateBookmarkTest {

        @Test
        @DisplayName("아직 북마크하지 않은 경우 지난주 월요일 날짜로 북마크를 생성하고 201 Created를 응답한다")
        void createBookmark1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            LocalDate lastWeekMonday = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().post("/api/v1/admin/bookmarks?accountId=" + adminAccountId + "&contentId=1")
                    .then()
                    .statusCode(201);

            FavoriteContent favoriteContent = favoriteContentRepository.findByAccountIdAndContentId(adminAccountId, 1L)
                    .orElseThrow();
            assertThat(favoriteContent.getCreatedAt()).isEqualTo(lastWeekMonday);
        }

        @Test
        @DisplayName("이미 북마크한 경우에도 201 Created를 응답하고 북마크는 하나만 유지된다")
        void createBookmark2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            jdbcTemplate.update("INSERT INTO favorite_content (created_at, account_id, content_id) VALUES (?, ?, ?)",
                    "2024-01-01", adminAccountId, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().post("/api/v1/admin/bookmarks?accountId=" + adminAccountId + "&contentId=1")
                    .then()
                    .statusCode(201);

            FavoriteContent favoriteContent = favoriteContentRepository.findByAccountIdAndContentId(adminAccountId, 1L)
                    .orElseThrow();
            assertThat(favoriteContent.getCreatedAt()).isEqualTo(LocalDate.of(2024, 1, 1));
        }

        @Test
        @DisplayName("ADMIN이 아닌 accountId로 등록하면 404 Not Found를 응답한다")
        void createBookmark3() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Long userAccountId = testDataHelper.insertAccount(Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().post("/api/v1/admin/bookmarks?accountId=" + userAccountId + "&contentId=1")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 등록을 요청하면 403 Forbidden을 응답한다")
        void createBookmark4() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().post("/api/v1/admin/bookmarks?accountId=" + userAccountId + "&contentId=1")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/bookmarks DELETE ADMIN 계정 북마크 삭제 테스트")
    class DeleteBookmarkTest {

        @Test
        @DisplayName("북마크가 존재하면 삭제하고 204 No Content를 응답한다")
        void deleteBookmark1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            jdbcTemplate.update("INSERT INTO favorite_content (created_at, account_id, content_id) VALUES (?, ?, ?)",
                    "2024-01-01", adminAccountId, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().delete("/api/v1/admin/bookmarks?accountId=" + adminAccountId + "&contentId=1")
                    .then()
                    .statusCode(204);

            assertThat(favoriteContentRepository.findByAccountIdAndContentId(adminAccountId, 1L)).isEmpty();
        }

        @Test
        @DisplayName("북마크가 존재하지 않아도 204 No Content를 응답한다")
        void deleteBookmark2() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().delete("/api/v1/admin/bookmarks?accountId=" + adminAccountId + "&contentId=1")
                    .then()
                    .statusCode(204);
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 삭제를 요청하면 403 Forbidden을 응답한다")
        void deleteBookmark3() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().delete("/api/v1/admin/bookmarks?accountId=" + userAccountId + "&contentId=1")
                    .then()
                    .statusCode(403);
        }
    }
}
