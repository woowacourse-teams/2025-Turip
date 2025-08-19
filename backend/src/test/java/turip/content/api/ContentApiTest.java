package turip.content.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM trip_course");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM region");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM place_category");

        jdbcTemplate.update("ALTER TABLE trip_course ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE region ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/contents/count GET 컨텐츠 수 조회 테스트")
    @Nested
    class readCount {

        @DisplayName("지역별 컨텐츠 수 조회 성공 시 200 OK 코드와 컨텐츠 수를 응답한다")
        @Test
        void readCountByRegionName() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO region (name) VALUES ('seoul')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, region_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, region_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("region", "seoul")
                    .when().get("/contents/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(2));
        }
    }

    @DisplayName("/contents/{id} GET 컨텐츠 단건 조회 테스트")
    @Nested
    class readContentById {
        @DisplayName("id로 컨텐츠 단건 조회 성공 시 200 OK 코드와 컨텐츠 정보를 응답한다")
        @Test
        void readContentById() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO Creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO Region (name) VALUES (?)",
                    "seoul");
            jdbcTemplate.update(
                    "INSERT INTO Content (creator_id, region_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            // when & then
            RestAssured.given().port(port)
                    .when().get("/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creatorId", is(1))
                    .body("regionId", is(1))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"));
        }
    }
}
