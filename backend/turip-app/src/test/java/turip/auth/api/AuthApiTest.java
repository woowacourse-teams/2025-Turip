package turip.auth.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import turip.auth.token.GoogleTokenParser;
import turip.account.domain.Provider;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @MockitoBean
    private GoogleTokenParser googleTokenParser;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jdbcTemplate.update("DELETE FROM refresh_token");
        jdbcTemplate.update("DELETE FROM favorite_content");
        jdbcTemplate.update("DELETE FROM favorite_folder");
        jdbcTemplate.update("DELETE FROM member");
        jdbcTemplate.update("DELETE FROM guest");
        jdbcTemplate.update("DELETE FROM account");

        jdbcTemplate.update("ALTER TABLE refresh_token ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE member ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE guest ALTER COLUMN id RESTART WITH 1");
        jdbcTemplate.update("ALTER TABLE account ALTER COLUMN id RESTART WITH 1");
    }

    @Nested
    @DisplayName("/login/google POST 구글 로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("신규 회원 로그인 성공 시 201 Created와 토큰을 응답한다")
        void loginNewMemberSuccess() {
            // given
            String idToken = "valid-google-id-token";
            String deviceFid = "device-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-123");
            when(googleTokenParser.getEmail(idToken)).thenReturn("newuser@gmail.com");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("isNewMember", is(true));
        }

        @Test
        @DisplayName("기존 회원 로그인 성공 시 200 OK와 토큰을 응답한다")
        void loginExistingMemberSuccess() {
            // given
            jdbcTemplate.update("INSERT INTO account (id) VALUES (1)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (1, 1, 'GOOGLE', 'google-user-existing', 'existing@gmail.com')");

            String idToken = "valid-google-id-token";
            String deviceFid = "device-456";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-existing");
            when(googleTokenParser.getEmail(idToken)).thenReturn("existing@gmail.com");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("isNewMember", is(false));
        }

        @Test
        @DisplayName("유효하지 않은 ID 토큰인 경우 401 Unauthorized를 응답한다")
        void loginWithInvalidIdToken() {
            // given
            String invalidIdToken = "invalid-token";
            String deviceFid = "device-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(anyString()))
                    .thenThrow(new turip.common.exception.custom.UnauthorizedException(
                            turip.common.exception.ErrorTag.ID_TOKEN_NOT_VALID));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", invalidIdToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(401)
                    .body("tag", is("ID_TOKEN_NOT_VALID"));
        }
    }

    @Nested
    @DisplayName("/token POST 토큰 갱신 테스트")
    class RefreshTest {

        @Test
        @DisplayName("유효한 refresh token으로 새로운 토큰을 발급받는다")
        void refreshTokenSuccess() {
            // given
            // 1. 먼저 로그인해서 토큰 받기
            jdbcTemplate.update("INSERT INTO account (id) VALUES (1)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (1, 1, 'GOOGLE', 'google-user-refresh', 'refresh@gmail.com')");

            String idToken = "valid-google-id-token";
            String deviceFid = "device-refresh-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-refresh");
            when(googleTokenParser.getEmail(idToken)).thenReturn("refresh@gmail.com");

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            String refreshToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .extract().path("refreshToken");

            // 2. refresh token으로 갱신
            Map<String, String> refreshRequest = new HashMap<>();
            refreshRequest.put("refreshToken", refreshToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(refreshRequest)
                    .when().post("/token")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue());
        }

        @Test
        @DisplayName("잘못된 refresh token인 경우 401 Unauthorized를 응답한다")
        void refreshTokenInvalid() {
            // given
            String invalidRefreshToken = "invalid.refresh.token";
            String deviceFid = "device-123";

            Map<String, String> refreshRequest = new HashMap<>();
            refreshRequest.put("refreshToken", invalidRefreshToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(refreshRequest)
                    .when().post("/token")
                    .then().log().all()
                    .statusCode(401);
        }

        @Test
        @DisplayName("DB에 저장된 토큰과 일치하지 않으면 401 Unauthorized를 응답한다")
        void refreshTokenMismatch() {
            // given
            jdbcTemplate.update("INSERT INTO account (id) VALUES (1)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (1, 1, 'GOOGLE', 'google-user-mismatch', 'mismatch@gmail.com')");

            String idToken = "valid-google-id-token";
            String deviceFid = "device-mismatch-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-mismatch");
            when(googleTokenParser.getEmail(idToken)).thenReturn("mismatch@gmail.com");

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200);

            // 다른 사용자의 refresh token 사용 시도
            jdbcTemplate.update("INSERT INTO account (id) VALUES (2)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (2, 2, 'GOOGLE', 'google-user-other', 'other@gmail.com')");

            String otherIdToken = "other-valid-google-id-token";
            String otherDeviceFid = "device-other-123";

            when(googleTokenParser.getProviderId(otherIdToken)).thenReturn("google-user-other");
            when(googleTokenParser.getEmail(otherIdToken)).thenReturn("other@gmail.com");

            Map<String, String> otherLoginRequest = new HashMap<>();
            otherLoginRequest.put("idToken", otherIdToken);

            String otherRefreshToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", otherDeviceFid)
                    .body(otherLoginRequest)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .extract().path("refreshToken");

            // 첫 번째 사용자의 deviceFid로 두 번째 사용자의 refresh token 사용 시도
            Map<String, String> refreshRequest = new HashMap<>();
            refreshRequest.put("refreshToken", otherRefreshToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(refreshRequest)
                    .when().post("/token")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("/logout POST 로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("정상적으로 로그아웃에 성공하면 204 No Content를 응답한다")
        void logoutSuccess() {
            // given
            jdbcTemplate.update("INSERT INTO account (id) VALUES (1)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (1, 1, 'GOOGLE', 'google-user-logout', 'logout@gmail.com')");

            String idToken = "valid-google-id-token";
            String deviceFid = "device-logout-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-logout");
            when(googleTokenParser.getEmail(idToken)).thenReturn("logout@gmail.com");

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            String accessToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .extract().path("accessToken");

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        @DisplayName("로그아웃 후 refresh token이 DB에서 삭제된다")
        void logoutDeletesRefreshToken() {
            // given
            jdbcTemplate.update("INSERT INTO account (id) VALUES (1)");
            jdbcTemplate.update(
                    "INSERT INTO member (id, account_id, provider, provider_id, email) VALUES (1, 1, 'GOOGLE', 'google-user-logout-delete', 'logout-delete@gmail.com')");

            String idToken = "valid-google-id-token";
            String deviceFid = "device-logout-delete-123";

            when(googleTokenParser.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParser.getProviderId(idToken)).thenReturn("google-user-logout-delete");
            when(googleTokenParser.getEmail(idToken)).thenReturn("logout-delete@gmail.com");

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            String accessToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .extract().path("accessToken");

            // refresh token이 저장되었는지 확인
            Integer countBefore = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM refresh_token WHERE device_fid = ?",
                    Integer.class,
                    deviceFid
            );
            assertThat(countBefore).isEqualTo(1);

            // when - 로그아웃
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(204);

            // then - refresh token이 삭제되었는지 확인
            Integer countAfter = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM refresh_token WHERE device_fid = ?",
                    Integer.class,
                    deviceFid
            );
            assertThat(countAfter).isEqualTo(0);
        }

        @Test
        @DisplayName("access token 없이 로그아웃 요청 시 401 Unauthorized를 응답한다")
        void logoutWithoutAccessToken() {
            // given
            String deviceFid = "device-no-token";

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(401);
        }

        @Test
        @DisplayName("유효하지 않은 access token으로 로그아웃 요청 시 401 Unauthorized를 응답한다")
        void logoutWithInvalidAccessToken() {
            // given
            String deviceFid = "device-invalid-token";
            String invalidAccessToken = "invalid.access.token";

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .header("Authorization", "Bearer " + invalidAccessToken)
                    .when().post("/logout")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
