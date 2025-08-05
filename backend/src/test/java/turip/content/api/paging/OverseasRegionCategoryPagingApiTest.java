package turip.content.api.paging;

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
class OverseasRegionCategoryPagingApiTest {

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

        // 크리에이터, 도시 데이터 설정
        jdbcTemplate.update(
                "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('일본')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('프랑스')");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('오사카', 1, null)");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 2, null)");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('파리', 3, null)");

        // 오사카 컨텐츠 데이터 설정
        for (int i = 1; i <= 9; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=seoul" + i, "오사카 여행 " + i
            );
        }

        // 서울 컨텐츠 데이터 설정
        for (int i = 1; i <= 10; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=jeju" + i, "서울 여행 " + i
            );
        }

        // 파리 컨텐츠 데이터 설정
        for (int i = 1; i <= 8; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 3, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=jeju" + i, "파리 여행 " + i
            );
        }
    }

    @DisplayName("/contents GET 일본(해외 카테고리 나라) 지역별 컨텐츠 목록 페이징 테스트")
    @Test
    void readContentsBySeoulRegionCategory() {
        // when: 첫 번째 페이지 (lastId=0, size=5)
        var firstPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "일본")
                .queryParam("size", 5)
                .queryParam("lastId", 0)
                .when().get("/contents");

        // then: 첫 번째 페이지
        firstPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("loadable", is(true))
                .body("regionCategoryName", is("일본"));

        // 첫 번째 페이지 마지막 content id
        Integer lastContentId = firstPageResponse.jsonPath().get("contents[4].content.id");

        // when: 두 번째 페이지
        var secondPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "일본")
                .queryParam("size", 5)
                .queryParam("lastId", lastContentId)
                .when().get("/contents");

        // then: 두 번째 페이지
        secondPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(4))
                .body("loadable", is(false))
                .body("regionCategoryName", is("일본"));
    }

    @DisplayName("/contents GET 해외 기타 지역별 컨텐츠 목록 페이징 테스트")
    @Test
    void readContentsByDomesticOtherRegionCategory() {
        // when: 첫 번째 페이지 (lastId=0, size=5)
        var firstPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "해외 기타")
                .queryParam("size", 5)
                .queryParam("lastId", 0)
                .when().get("/contents");

        // then: 첫 번째 페이지
        firstPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("loadable", is(true))
                .body("regionCategoryName", is("해외 기타"));

        // 첫 번째 페이지 마지막 content id
        Integer lastContentId = firstPageResponse.jsonPath().get("contents[4].content.id");

        // when: 두 번째 페이지
        var secondPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "해외 기타")
                .queryParam("size", 5)
                .queryParam("lastId", lastContentId)
                .when().get("/contents");

        // then: 두 번째 페이지
        secondPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(3))
                .body("loadable", is(false))
                .body("regionCategoryName", is("해외 기타"));
    }
}
