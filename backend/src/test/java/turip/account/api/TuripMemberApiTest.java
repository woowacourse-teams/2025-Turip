package turip.account.api;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import turip.common.exception.ErrorTag;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TuripMemberApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jdbcTemplate.update("DELETE FROM refresh_token");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_place");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM turip_member");
        jdbcTemplate.update("DELETE FROM social_member");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM guest");
        jdbcTemplate.update("DELETE FROM account");
        jdbcTemplate.update("DELETE FROM content");
        jdbcTemplate.update("DELETE FROM creator");
        jdbcTemplate.update("DELETE FROM city");
        jdbcTemplate.update("DELETE FROM country");

        jdbcTemplate.update("ALTER TABLE refresh_token ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_place ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE favorite_folder ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE turip_member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE social_member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE guest ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE account ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE content ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE creator ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE city ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE country ALTER COLUMN id RESTART WITH 1");
    }

    @Nested
    @DisplayName("/turip-members POST 자체 회원가입 테스트")
    class create {

        @DisplayName("자체 회원가입 할 수 있다")
        @Test
        void create1() {
            // given
            String email = "turip@gmail.com";
            String loginId = "turip";
            String loginPassword = "ValidPass1!";

            Map<String, String> request = new HashMap<>(
                    Map.of("email", email, "loginId", loginId, "loginPassword", loginPassword));

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/turip-members")
                    .then()
                    .statusCode(201)
                    .body("id", is(1))
                    .body("memberId", is(1))
                    .body("loginId", is(loginId));
        }

        @DisplayName("회원가입 형식이 올바르지 않은 경우 400 Bad Request를 응답한다.")
        @ParameterizedTest
        @CsvSource({
                "turip2gmail.com, turip, ValidPass1!",
                "turip@gmail.com, turip~, ValidPass1!",
                "turip@gmail.com, turip, 1234"
        })
        void create2(String email, String loginId, String loginPassword) {
            // given
            Map<String, String> request = new HashMap<>(
                    Map.of("email", email, "loginId", loginId, "loginPassword", loginPassword));

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/turip-members")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("동일한 아이디가 존재하는 경우 409 Conflict를 응답한다.")
        @Test
        void create3() {
            // given
            String loginId = "turip";
            testDataHelper.insertTuripMember("turip1@gmail.com", loginId, "ValidPass1!");

            Map<String, String> request = new HashMap<>(
                    Map.of("email", "turip2@gmail.com", "loginId", loginId, "loginPassword", "ValidPass2!"));

            // when & then
            RestAssured.given().port(port)
                    .header("device-fid", "testDeviceFid")
                    .body(request)
                    .contentType(ContentType.JSON)
                    .when().post("/turip-members")
                    .then()
                    .statusCode(409)
                    .body("tag", is(ErrorTag.LOGIN_ID_CONFLICT.name()))
                    .body("message", is(ErrorTag.LOGIN_ID_CONFLICT.getMessage()));
        }
    }
}
