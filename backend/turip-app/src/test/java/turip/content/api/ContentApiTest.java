package turip.content.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
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
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        testDataHelper.cleanDatabase();
    }

    @DisplayName("/api/v1/contents/{contentId} GET 컨텐츠 단건 조회 테스트")
    @Nested
    class ReadContentById {
        @DisplayName("contentId로 컨텐츠 단건 조회 성공 시 200 OK 코드와 컨텐츠 정보를 응답한다")
        @Test
        void readContentById1() {
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
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, ?)", accountId, "testDeviceFid");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isBookmarked", is(false));
        }

        @DisplayName("해당 계정에 찜이 되어 있는 경우 isBookmarked가 true로 응답된다")
        @Test
        void readContentById_withDeviceFidHeader() {
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
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, ?)", accountId, "testDeviceFid");
            jdbcTemplate.update("INSERT INTO favorite_content (created_at, account_id, content_id) VALUES (?, ?, ?)",
                    "2025-07-01", accountId, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isBookmarked", is(true));
        }

        @DisplayName("id에 해당하는 컨텐츠가 없는 경우 404 NOT FOUND 코드를 응답한다")
        @Test
        void readContentById2() {
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
            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, ?)", accountId, "testDeviceFid");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/api/v1/contents/{id}", 20)
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/api/v1/contents/popular GET 주간 인기 컨텐츠 조회 테스트")
    @Nested
    class ReadWeeklyPopularFavoriteContentContents {

        @DisplayName("device-fid 헤더가 존재하면 컨텐츠 목록과 찜 여부를 응답한다. 성공 시 200 OK 코드를 응답한다")
        @Test
        void getPopularContentsWithDeviceFid2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', '여행채널')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update("INSERT INTO content (creator_id, city_id, url, title, uploaded_date) " +
                    "VALUES (1, 1, 'https://youtube.com/watch?v=test', '서울 여행', '2025-07-28')");

            Long accountId = testDataHelper.insertAccount();
            jdbcTemplate.update("INSERT INTO guest (account_id, device_fid) VALUES (?, ?)", accountId, "testDeviceFid");
            jdbcTemplate.update("INSERT INTO favorite_content (created_at, account_id, content_id) VALUES (?, ?, ?)",
                    LocalDate.now().minusDays(7), accountId, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("size", 5)
                    .when().get("/api/v1/contents/popular")
                    .then()
                    .statusCode(200);
        }
    }
}
