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
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/contents/{contentId} GET 컨텐츠 단건 조회 테스트")
    @Nested
    class ReadContentById {
        @DisplayName("contentId로 컨텐츠 단건 조회 성공 시 200 OK 코드와 컨텐츠 정보를 응답한다")
        @Test
        void readContentById1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isFavorite", is(false));
        }

        @DisplayName("device-fid 헤더가 존재하며 찜이 되어 있는 경우 isFavorite이 true로 응답된다")
        @Test
        void readContentById_withDeviceFidHeader() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");
            jdbcTemplate.update(
                    "INSERT INTO member (device_fid) VALUES (?)", "testDeviceFid");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id) VALUES (?, ?)", 1, 1);

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/contents/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("creator.id", is(1))
                    .body("city.name", is("서울"))
                    .body("title", is("서울 데이트 코스 추천"))
                    .body("url", is("https://youtube.com/watch?v=abcd1"))
                    .body("uploadedDate", is("2024-07-01"))
                    .body("isFavorite", is(true));
        }

        @DisplayName("id에 해당하는 컨텐츠가 없는 경우 404 NOT FOUND 코드를 응답한다")
        @Test
        void readContentById2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO Creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO Content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/contents/{id}", 20)
                    .then()
                    .statusCode(404);
        }
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
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
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
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
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
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '메이의 서촌 당일치기 코스 추천', '2025-06-18')");

            // when & then
            RestAssured.given().port(port)
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
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id) VALUES ('서울', 1)");

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
                    .queryParam("keyword", "명동교자")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1));

            // 2. title로 검색 시
            RestAssured.given().port(port)
                    .queryParam("keyword", "서울")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2));

            // 3. creator name으로 검색 시
            RestAssured.given().port(port)
                    .queryParam("keyword", "여행블로거")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(2));
        }

        @DisplayName("place name 검색 시 연관 장소가 없으면 반환되지 않음")
        @Test
        void readByKeyword_placeNameWithoutRelatedPlace() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('', '맛집탐방가')");
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('대한민국', '')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, '')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, '', '부산 여행 후기', '2025-07-01')");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("keyword", "해운대해수욕장")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(0))
                    .body("loadable", is(false));

            RestAssured.given().port(port)
                    .queryParam("keyword", "부산")
                    .queryParam("size", 2)
                    .queryParam("lastId", 0)
                    .when().get("/contents/keyword")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1));
        }
    }

    @DisplayName("/contents/popular/favorites GET 주간 인기 컨텐츠 조회 테스트")
    @Nested
    class ReadWeeklyPopularFavoriteContentContents {

        @DisplayName("device-fid 헤더가 존재하지 않는 경우 400 BAD REQUEST 코드를 응답한다")
        @Test
        void getPopularContentsWithoutDeviceFid() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', '여행채널')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 1, null)");
            jdbcTemplate.update("INSERT INTO content (creator_id, city_id, url, title, uploaded_date) " +
                    "VALUES (1, 1, 'https://youtube.com/watch?v=test', '서울 여행', '2025-07-28')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_DATE - 7)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("size", 5)
                    .when().log().all().get("/contents/popular/favorites")
                    .then().log().all()
                    .statusCode(400);
        }

        @DisplayName("device-fid 헤더가 존재하면 컨텐츠 목록과 찜 여부를 응답한다. 성공 시 200 OK 코드를 응답한다")
        @Test
        void getPopularContentsWithDeviceFid2() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', '여행채널')");
            jdbcTemplate.update("INSERT INTO country (name) VALUES ('대한민국')");
            jdbcTemplate.update("INSERT INTO city (name, country_id, province_id) VALUES ('서울', 1, null)");
            jdbcTemplate.update("INSERT INTO content (creator_id, city_id, url, title, uploaded_date) " +
                    "VALUES (1, 1, 'https://youtube.com/watch?v=test', '서울 여행', '2025-07-28')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_DATE - 7)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("size", 5)
                    .when().get("/contents/popular/favorites")
                    .then()
                    .statusCode(200);
        }
    }
}
