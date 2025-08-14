package turip.content.api.paging;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DomesticRegionCategoryPagingApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");

        // 크리에이터, 도시 데이터 설정
        jdbcTemplate.update(
                "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
        jdbcTemplate.update("INSERT INTO country (name) VALUES ('일본')");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 1, null)");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('세종', 1, null)");
        jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('오사카', 2, null)");

        // 서울 컨텐츠 데이터 설정
        for (int i = 1; i <= 9; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=seoul" + i, "서울 여행 " + i
            );
        }

        // 국내 기타 (세종) 컨텐츠 데이터 설정
        for (int i = 1; i <= 8; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 2, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=jeju" + i, "세종 여행 " + i
            );
        }

        // 일본 컨텐츠 데이터 설정
        for (int i = 1; i <= 8; i++) {
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 3, ?, ?, '2024-07-01')",
                    "https://youtube.com/watch?v=jeju" + i, "일본 여행 " + i
            );
        }
    }

    @DisplayName("/contents GET 서울(국내 카테고리 도시) 지역별 컨텐츠 목록 페이징 테스트")
    @Test
    void readContentsBySeoulRegionCategory() {
        // when: 첫 번째 페이지 (lastId=0, size=5)
        Response firstPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "서울")
                .queryParam("size", 5)
                .queryParam("lastId", 0)
                .when().get("/contents");

        // then: 첫 번째 페이지
        firstPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("loadable", is(true))
                .body("regionCategoryName", is("서울"));

        // 첫 번째 페이지 마지막 content id
        Integer lastContentId = firstPageResponse.jsonPath().get("contents[4].content.id");

        // when: 두 번째 페이지
        Response secondPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "서울")
                .queryParam("size", 5)
                .queryParam("lastId", lastContentId)
                .when().get("/contents");

        // then: 두 번째 페이지
        secondPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(4))
                .body("loadable", is(false))
                .body("regionCategoryName", is("서울"));
    }

    @DisplayName("/contents GET 국내 기타 지역별 컨텐츠 목록 페이징 테스트")
    @Test
    void readContentsByDomesticOtherRegionCategory() {
        // when: 첫 번째 페이지 (lastId=0, size=5)
        Response firstPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "국내 기타")
                .queryParam("size", 5)
                .queryParam("lastId", 0)
                .when().get("/contents");

        // then: 첫 번째 페이지
        firstPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(5))
                .body("loadable", is(true))
                .body("regionCategoryName", is("국내 기타"));

        // 첫 번째 페이지 마지막 content id
        Integer lastContentId = firstPageResponse.jsonPath().get("contents[4].content.id");

        // when: 두 번째 페이지
        Response secondPageResponse = RestAssured.given().port(port)
                .queryParam("regionCategory", "국내 기타")
                .queryParam("size", 5)
                .queryParam("lastId", lastContentId)
                .when().get("/contents");

        // then: 두 번째 페이지
        secondPageResponse.then()
                .statusCode(200)
                .body("contents.size()", is(3))
                .body("loadable", is(false))
                .body("regionCategoryName", is("국내 기타"));
    }
} 
