package turip.tripcourse.api;

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
public class TripCourseApiTest {

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

        jdbcTemplate.update("ALTER TABLE trip_course ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE region ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/trip-courses GET 여행 상세 조회 테스트")
    @Nested
    class readTripCourse {

        @DisplayName("성공 시 200 OK 코드와 여행 상세 정보를 응답한다")
        @Test
        void readTripCourseDetails() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update("INSERT INTO region (name) VALUES ('seoul')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude, categories) VALUES ('루터회관','https://naver.me/5UrZAIeY','루터회관의 도로명 주소,',38.1234,127.23123, '[\"관광\"]')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude, categories) VALUES ('테디뵈르하우스','https://naver.me/5UrZAIeY','테디뵈르하우스의 도로명 주소,',38.11,127.22, '[\"베이커리\", \"카페\"]')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, region_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, region_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");
            jdbcTemplate.update(
                    "INSERT INTO trip_course (content_id, place_id, visit_day, visit_order) VALUES (1, 1, 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO trip_course (content_id, place_id, visit_day, visit_order) VALUES (1, 2, 2, 1)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("contentId", "1")
                    .when().get("/trip-courses")
                    .then()
                    .statusCode(200)
                    .body("tripPlaceCount", is(2));
        }
    }
}
