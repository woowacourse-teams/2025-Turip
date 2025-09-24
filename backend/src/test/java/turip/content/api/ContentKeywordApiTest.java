package turip.content.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
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
import org.springframework.transaction.annotation.Transactional;
import turip.container.TestContainerConfig;

@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional(propagation = org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED)
@Import(TestContainerConfig.class)
public class ContentKeywordApiTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @LocalServerPort
    protected int port;

    @BeforeAll
    void setUp() {
        // Fulltext index 생성
        jdbcTemplate.execute("ALTER TABLE content ADD FULLTEXT INDEX idx_title (title) WITH PARSER ngram");
        jdbcTemplate.execute(
                "ALTER TABLE creator ADD FULLTEXT INDEX idx_channel_name (channel_name) WITH PARSER ngram");
        jdbcTemplate.execute("ALTER TABLE place ADD FULLTEXT INDEX idx_name (name) WITH PARSER ngram");
    }

    @BeforeEach
    void clear() {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.execute("TRUNCATE TABLE place_category");
        jdbcTemplate.execute("TRUNCATE TABLE content_place");
        jdbcTemplate.execute("TRUNCATE TABLE place");
        jdbcTemplate.execute("TRUNCATE TABLE category");
        jdbcTemplate.execute("TRUNCATE TABLE favorite_folder");
        jdbcTemplate.execute("TRUNCATE TABLE favorite_content");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("TRUNCATE TABLE content");
        jdbcTemplate.execute("TRUNCATE TABLE creator");
        jdbcTemplate.execute("TRUNCATE TABLE city");
        jdbcTemplate.execute("TRUNCATE TABLE country");
        jdbcTemplate.execute("TRUNCATE TABLE province");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }


    @DisplayName("/contents/keyword/count GET 키워드로 검색된 컨텐츠 수 조회 테스트")
    @Nested
    class ReadCountByKeyword {

        @DisplayName("키워드에 대한 컨텐츠 수 조회 성공 시 200 OK 코드와 컨텐츠 수를 응답한다")
        @Test
        void readCountByKeyword1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '여행하는 뭉치')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천 with 메이', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("keyword", "메이")
                    .when().get("/contents/keyword/count")
                    .then()
                    .statusCode(200)
                    .body("count", is(1));
        }
    }

    @DisplayName("/contents/keyword GET 키워드 기반 컨텐츠 검색 테스트")
    @Nested
    class ReadByKeyword {

        @DisplayName("키워드 기반 검색 성공 시, 200 코드와 컨텐츠 정보들과 더 받아올 정보가 있는지 여부를 응답한다")
        @Test
        void readByKeyword_unloadable() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '메이')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "메이")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2))
                    .body("loadable", is(false));
        }

        @DisplayName("키워드 기반 검색 성공 시, 200 코드와 컨텐츠 정보들과 더 받아올 정보가 있는지 여부를 응답한다")
        @Test
        void readByKeyword_loadable() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', '메이')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "메이")
                    .queryParam("size", 1)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1))
                    .body("loadable", is(true));
        }

        @DisplayName("place name 조건이 추가된 findByKeywordContaining 테스트 - 연관 장소가 있을 때만 반환")
        @Test
        void readByKeyword_placeNameConditionTest() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('', '여행블로거')");
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('대한민국', '')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, '')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");

            // 장소가 포함된 컨텐츠
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, '', '서울 맛집 투어', '2025-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('명동교자', '', '서울 중구', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content_place (visit_day, visit_order, time_line, place_id, content_id) VALUES (1, 1, '00:10:00', 1, 1)");

            // 장소가 포함되지 않은 컨텐츠
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, '', '서울 관광 가이드', '2025-07-02')");

            // when & then
            // 1. place name으로 검색 시
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "명동교자")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1));

            // 2. title로 검색 시
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "서울")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2));

            // 3. creator name으로 검색 시
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "여행블로거")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2));
        }

        @DisplayName("place name 검색 시 연관 장소가 없으면 반환되지 않는다")
        @Test
        void readByKeyword_placeNameWithoutRelatedPlace() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('', '맛집탐방가')");
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('대한민국', '')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, '')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, '', '부산 여행 후기', '2025-07-01')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "해운대해수욕장")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(0))
                    .body("loadable", is(false));

            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("keyword", "부산")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1));
        }
    }
}
