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
class ContentPlaceApiTest {

    @LocalServerPort
    private int port;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM place_category");
        jdbcTemplate.update("DELETE FROM content_place");
        jdbcTemplate.update("DELETE FROM category");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM place");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");
        jdbcTemplate.update("DELETE FROM province");

        jdbcTemplate.update("ALTER TABLE favorite_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE province ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/contents/places GET 여행 상세 조회 테스트")
    @Nested
    class ReadContentPlace {

        @DisplayName("성공 시 200 OK 코드와 여행 상세 정보를 응답한다")
        @Test
        void readContentPlaceDetails1() {
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
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('루터회관','https://mapurl/5UrZAI','루터회관의 도로명 주소',38.1234,127.23123)");
            jdbcTemplate.update(
                    "INSERT INTO place (name, url, address, latitude, longitude) VALUES ('테디뵈르하우스','https://mapurl/5UrZAIeZ','테디뵈르하우스의 도로명 주소',38.11,127.22)");
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (1, 1)"); // 루터회관 - 관광
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (2, 2)"); // 테디뵈르하우스 - 베이커리
            jdbcTemplate.update("INSERT INTO place_category (place_id, category_id) VALUES (2, 3)"); // 테디뵈르하우스 - 카페
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서울 데이트 코스 추천', '2024-07-01')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (1, 1, 'https://youtube.com/watch?v=abcd1', '서촌 당일치기 코스 추천', '2025-06-18')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 1, 1, 1, '00:11:00')");
            jdbcTemplate.update(
                    "INSERT INTO content_place (content_id, place_id, visit_day, visit_order, time_line) VALUES (1, 2, 2, 1, '00:12:00')");
            jdbcTemplate.update(
                    "INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '기본 폴더', true)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_place (favorite_folder_id, place_id) VALUES (1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("contentId", "1")
                    .when().get("/contents/places")
                    .then()
                    .statusCode(200)
                    .body("contentPlaceCount", is(2))
                    .body("contentPlaces[0].isFavoritePlace", is(true))
                    .body("contentPlaces[1].isFavoritePlace", is(false))
                    .body("contentPlaces[0].timeLine", is("11:00"))
                    .body("contentPlaces[1].timeLine", is("12:00"))
                    .body("contentPlaces[0].visitDay", is(1))
                    .body("contentPlaces[1].visitDay", is(2))
                    .body("contentPlaces[0].visitOrder", is(1))
                    .body("contentPlaces[1].visitOrder", is(1))
                    .body("contentPlaces[0].place.name", is("루터회관"))
                    .body("contentPlaces[1].place.name", is("테디뵈르하우스"));
        }

        @DisplayName("contentId에 대한 컨텐츠가 존재하지 않는 경우 404 NOT FOUND를 응답한다")
        @Test
        void readContentPlaceDetails2() {
            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .queryParam("contentId", "1")
                    .when().get("/contents/places")
                    .then()
                    .statusCode(404);
        }
    }
}
