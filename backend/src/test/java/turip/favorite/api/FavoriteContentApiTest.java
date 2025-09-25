package turip.favorite.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.Matchers;
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
class FavoriteContentApiTest {

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/favorites/contents POST 찜 생성 테스트")
    @Nested
    class createFavoriteContent {

        @DisplayName("성공 시 201 Created 코드와 찜 생성 정보를 응답한다")
        @Test
        void addFavoriteContent() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update("INSERT INTO category (name) VALUES ('관광지')");
            jdbcTemplate.update("INSERT INTO category (name) VALUES ('빵집')");
            jdbcTemplate.update("INSERT INTO category (name) VALUES ('카페')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://naver.me/5UrZAIeY','루터회관의 도로명 주소',38.1234,127.23123)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('테디뵈르하우스','https://naver.me/5UrZAI','테디뵈르하우스의 도로명 주소',38.11,127.22)");
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (1, 1)"); // 루터회관 - 관광
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (2, 2)"); // 테디뵈르하우스 - 베이커리
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (2, 3)"); // 테디뵈르하우스 - 카페
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 1, 1, 1, '00:10:00')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 2, 2, 1, '00:10:00')");

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("contentId", "1"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/favorites/contents")
                    .then()
                    .statusCode(201)
                    .header("Location", org.hamcrest.Matchers.containsString("/favorites/contents/"))
                    .body("id", Matchers.notNullValue())
                    .body("createdAt", Matchers.notNullValue())
                    .body("memberId", Matchers.notNullValue())
                    .body("content.id", is(1))
                    .body("content.title", is("서울 데이트 코스 추천"));
        }

        @DisplayName("contentId에 대한 컨텐츠가 존재하지 않는 경우 404 Not Found를 응답한다")
        @Test
        void contentNotFoundException() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update("INSERT INTO category (name) VALUES ('관광지')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('경복궁','https://naver.me/test','경복궁 도로명 주소',37.1234,126.1234)");
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=test123', '서울 여행', '2025-06-01')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 1, 1, 1, '00:10:00')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("contentId", "2"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/favorites/contents")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("이미 찜한 컨텐츠인 경우 409 CONFLICT를 응답한다")
        @Test
        void duplicateFavoriteException() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator1.jpg', 'TravelMate')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update("INSERT INTO category (name) VALUES ('관광지')");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('경복궁','https://naver.me/test','경복궁 도로명 주소',37.1234,126.1234)");
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=test123', '서울 여행', '2025-06-01')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 1, 1, 1, '00:10:00')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (created_at, member_id, content_id) VALUES ('2024-05-05', 1, 1)");

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("contentId", "1"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/favorites/contents")
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("/favorites/contents DELETE 찜 삭제 테스트")
    @Nested
    class deleteFavoriteContent {

        @DisplayName("성공 시 204 No Content를 응답한다")
        @Test
        void removeFavorite() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', 'Travel')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=deleteTest', '삭제 테스트 영상', '2025-06-01')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("contentId", 1)
                    .when().delete("/favorites/contents")
                    .then()
                    .statusCode(204);
        }

        @DisplayName("contentId에 대한 컨텐츠가 존재하지 않는 경우 404 Not Found를 응답한다")
        @Test
        void contentNotFoundException() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', 'Travel')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=deleteTest', '삭제 테스트 영상', '2025-06-01')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("contentId", 2)
                    .when().delete("/favorites/contents")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("device-fid에 대한 사용자가 존재하지 않는 경우 404 Not Found를 응답한다")
        @Test
        void memberNotFoundException() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', 'Travel')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=deleteTest', '삭제 테스트 영상', '2025-06-01')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "haruharu")
                    .queryParam("contentId", 1)
                    .when().delete("/favorites/contents")
                    .then()
                    .statusCode(404);
        }

        @DisplayName("해당 사용자가 찜한 컨텐츠가 아닌 경우 404 Not Found를 응답한다")
        @Test
        void favoriteNotFoundException() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', 'Travel')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=deleteTest', '삭제 테스트 영상', '2025-06-01')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "haruharu")
                    .queryParam("contentId", 2)
                    .when().delete("/favorites/contents")
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/favorites/contents GET 찜 조회 테스트")
    @Nested
    class ReadFavoriteContent {

        @DisplayName("성공 시 200 OK 코드와 찜한 콘텐츠 정보를 반환한다")
        @Test
        void readMyFavoriteContents_success() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES ('https://image.example.com/creator.jpg', 'Travel')");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, province_id, image_url) VALUES ('서울', 1, null,'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=test', '테스트 영상', '2025-08-01')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_content (member_id, content_id, created_at) VALUES (1, 1, CURRENT_TIMESTAMP)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('장소1','https://naver.me/place1','주소1',37.5,127.0)");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 1, 1, 1, '00:10:00')");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("size", 5)
                    .queryParam("lastId", 0)
                    .when().get("/favorites/contents")
                    .then()
                    .statusCode(200)
                    .body("contents.size()", is(1))
                    .body("contents[0].content.id", is(1))
                    .body("contents[0].content.title", is("테스트 영상"))
                    .body("contents[0].tripDuration.days", is(1))
                    .body("loadable", is(false));
        }
    }
}
