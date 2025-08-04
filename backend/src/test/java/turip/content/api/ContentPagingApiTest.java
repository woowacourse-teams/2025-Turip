package turip.content.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContentPagingApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM trip_course");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE trip_course ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");

        jdbcTemplate.update(
                "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('korea')");
        jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('seoul', 1)");
        for (int i = 1; i <= 10; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=abcd" + i, "서울 여행 " + i
            );
            jdbcTemplate.update("INSERT INTO place (name, latitude, longitude) VALUES (?, ?, ?)", "장소" + i, 34.5,
                    127.5);
            jdbcTemplate.update(
                    "INSERT INTO trip_course (visit_day, visit_order, place_id, content_id) VALUES (1, 1, ?, ?)", i, i);
        }
    }

    @DisplayName("/contents GET 지역별 컨텐츠 목록 페이징 테스트")
    @Test
    void readContentsByCityNamePaging() {
        // when: 첫 페이지 요청 (lastId=0, size=5)
        var firstPageResponse = RestAssured.given().port(port)
                .queryParam("cityName", "seoul")
                .queryParam("size", 5)
                .queryParam("lastId", 0)
                .when().get("/contents");

        // then: id 10~6, 5개, loadable=true
        firstPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("contents[0].content.id", is(10))
                .body("contents[4].content.id", is(6))
                .body("loadable", is(true));

        // when: 두 번째 페이지 요청 (lastId=6, size=5)
        var secondPageResponse = RestAssured.given().port(port)
                .queryParam("cityName", "seoul")
                .queryParam("size", 5)
                .queryParam("lastId", 6)
                .when().get("/contents");

        // then: id 5~1, 5개, loadable=false
        secondPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("contents[0].content.id", is(5))
                .body("contents[4].content.id", is(1))
                .body("loadable", is(false));
    }
}
