package turip.favoritefolder.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
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
public class FavoriteFolderApiTest {

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
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder");
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
        jdbcTemplate.update("ALTER TABLE category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE place_category ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
    }

    @DisplayName("/favorites-folders POST 장소 찜 폴더 생성 테스트")
    @Nested
    class Create {

        @DisplayName("커스텀 찜 폴더 생성에 성공한 경우 201 CREATED 코드와 생성된 폴더 정보를 응답한다")
        @Test
        void create1() {
            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "대구 맛집 모음"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-folders")
                    .then()
                    .statusCode(201);
        }

        @DisplayName("중복된 찜 폴더 이름이 존재하여 생성에 실패한 경우 201 CONFLICT 코드를 응답한다")
        @Test
        void create2() {
            // given
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '대구 맛집 모음', false)");

            // when & then
            Map<String, String> request = new HashMap<>(Map.of("name", "대구 맛집 모음"));
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/favorite-folders")
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("/favorites-folders GET 특정 회원의 장소 찜 폴더 조회 테스트")
    @Nested
    class ReadAllByMember {

        @DisplayName("조회에 성공한 경우 200 OK 코드와 폴더 목록을 응답한다")
        @Test
        void readAllByMember1() {
            // given
            jdbcTemplate.update("INSERT INTO member (device_fid) VALUES ('testDeviceFid')");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '기본 폴더', true)");
            jdbcTemplate.update(
                    "INSERT INTO favorite_folder (member_id, name, is_default) VALUES (1, '대구 맛집 모음', false)");

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .when().get("/favorite-folders")
                    .then()
                    .statusCode(200)
                    .body("favoriteFolders.size()", is(2))
                    .body("favoriteFolders[0].id", is(1))
                    .body("favoriteFolders[0].memberId", is(1))
                    .body("favoriteFolders[0].name", is("기본 폴더"))
                    .body("favoriteFolders[0].isDefault", is(true))
                    .body("favoriteFolders[1].id", is(2))
                    .body("favoriteFolders[1].memberId", is(1))
                    .body("favoriteFolders[1].name", is("대구 맛집 모음"))
                    .body("favoriteFolders[1].isDefault", is(false));
        }

        @DisplayName("저장되지 않은 회원이 조회를 시도하는 경우 200 OK 코드와 기본 폴더 정보를 응답한다")
        @Test
        void readAllByMember2() {
            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "newDeviceFid")
                    .when().get("/favorite-folders")
                    .then()
                    .statusCode(200)
                    .body("favoriteFolders.size()", is(1))
                    .body("favoriteFolders[0].memberId", is(1))
                    .body("favoriteFolders[0].name", is("기본 폴더"))
                    .body("favoriteFolders[0].isDefault", is(true));
        }
    }
}
