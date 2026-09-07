package turip.account.api;

import static org.assertj.core.api.Assertions.assertThat;
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
import turip.account.domain.Role;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FcmTokenApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("/api/v1/fcm-tokens POST FCM 토큰 등록/갱신 테스트")
    class RegisterTest {

        @Test
        @DisplayName("새로운 토큰 등록 시 200 OK와 함께 알림이 활성화된 토큰을 저장한다")
        void register1() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            String deviceFid = "device-1";

            Map<String, String> requestBody = new HashMap<>(Map.of("token", "fcm-token-1"));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(200);

            Map<String, Object> saved = jdbcTemplate.queryForMap(
                    "SELECT token, notification_enabled FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    accountId, deviceFid);
            assertThat(saved.get("token")).isEqualTo("fcm-token-1");
            assertThat(saved.get("notification_enabled")).isEqualTo(true);
        }

        @Test
        @DisplayName("이미 등록된 기기의 토큰 갱신 시 토큰만 갱신되고 알림 설정은 유지된다")
        void register2() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            String deviceFid = "device-1";
            testDataHelper.insertFcmToken(accountId, deviceFid, "old-token", false);

            Map<String, String> requestBody = new HashMap<>(Map.of("token", "new-token"));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(200);

            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    Long.class, accountId, deviceFid);
            assertThat(count).isEqualTo(1);

            Map<String, Object> saved = jdbcTemplate.queryForMap(
                    "SELECT token, notification_enabled FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    accountId, deviceFid);
            assertThat(saved.get("token")).isEqualTo("new-token");
            assertThat(saved.get("notification_enabled")).isEqualTo(false);
        }

        @Test
        @DisplayName("다른 기기에 등록된 토큰을 등록하면 기존 토큰 레코드를 재할당한다")
        void register3() {
            Long otherAccountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(otherAccountId, "other@turip.com", false);
            testDataHelper.insertFcmToken(otherAccountId, "other-device", "shared-token", true);

            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            String deviceFid = "device-1";

            Map<String, String> requestBody = new HashMap<>(Map.of("token", "shared-token"));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(200);

            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM fcm_token WHERE token = ?", Long.class, "shared-token");
            assertThat(count).isEqualTo(1);

            Long ownerAccountId = jdbcTemplate.queryForObject(
                    "SELECT account_id FROM fcm_token WHERE token = ?", Long.class, "shared-token");
            assertThat(ownerAccountId).isEqualTo(accountId);
        }

        @Test
        @DisplayName("액세스 토큰 없이 device-fid만 있는 게스트 요청도 토큰을 등록한다")
        void register4() {
            String deviceFid = "guest-device";
            Map<String, String> requestBody = new HashMap<>(Map.of("token", "guest-fcm-token"));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(200);

            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM fcm_token WHERE device_fid = ? AND token = ?",
                    Long.class, deviceFid, "guest-fcm-token");
            assertThat(count).isEqualTo(1);
        }

        @Test
        @DisplayName("토큰이 공백 값이면 400 Bad Request를 응답한다")
        void register5() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("token", "  ");

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", "device-1")
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(400)
                    .body("tag", is("FCM_TOKEN_BLANK"));
        }

        @Test
        @DisplayName("device-fid 헤더가 공백 값이면 400 Bad Request를 응답한다")
        void register6() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);

            Map<String, String> requestBody = new HashMap<>(Map.of("token", "fcm-token-1"));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", " ")
                    .body(requestBody)
                    .when().post("/api/v1/fcm-tokens")
                    .then().log().all()
                    .statusCode(400)
                    .body("tag", is("DEVICE_FID_REQUIRED"));
        }
    }

    @Nested
    @DisplayName("/api/v1/fcm-tokens/notification PATCH 알림 수신 여부 변경 테스트")
    class ChangeNotificationEnabledTest {

        @Test
        @DisplayName("등록된 기기의 알림 수신 여부를 변경하면 204 No Content를 응답한다")
        void changeNotificationEnabled1() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            String deviceFid = "device-1";
            testDataHelper.insertFcmToken(accountId, deviceFid, "fcm-token-1", true);

            Map<String, Object> requestBody = new HashMap<>(Map.of("notificationEnabled", false));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().patch("/api/v1/fcm-tokens/notification")
                    .then().log().all()
                    .statusCode(204);

            Boolean notificationEnabled = jdbcTemplate.queryForObject(
                    "SELECT notification_enabled FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    Boolean.class, accountId, deviceFid);
            assertThat(notificationEnabled).isFalse();
        }

        @Test
        @DisplayName("등록된 토큰이 없는 기기에 대해 요청하면 404 Not Found를 응답한다")
        void changeNotificationEnabled2() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);

            Map<String, Object> requestBody = new HashMap<>(Map.of("notificationEnabled", false));

            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", "unregistered-device")
                    .body(requestBody)
                    .when().patch("/api/v1/fcm-tokens/notification")
                    .then().log().all()
                    .statusCode(404)
                    .body("tag", is("FCM_TOKEN_NOT_FOUND"));
        }
    }

    @Nested
    @DisplayName("/api/v1/auth/logout POST 로그아웃 시 FCM 토큰 삭제 테스트")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃 시 해당 기기의 FCM 토큰이 삭제된다")
        void logout1() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            String deviceFid = "device-1";
            testDataHelper.insertFcmToken(accountId, deviceFid, "fcm-token-1", true);

            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", deviceFid)
                    .when().post("/api/v1/auth/logout")
                    .then().log().all()
                    .statusCode(204);

            Long count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    Long.class, accountId, deviceFid);
            assertThat(count).isEqualTo(0);
        }

        @Test
        @DisplayName("다른 기기의 FCM 토큰은 로그아웃의 영향을 받지 않는다")
        void logout2() {
            Long accountId = testDataHelper.insertAccount(Role.USER);
            testDataHelper.insertMember(accountId, "member@turip.com", false);
            String accessToken = testDataHelper.createAccessToken(accountId);
            testDataHelper.insertFcmToken(accountId, "device-1", "fcm-token-1", true);
            testDataHelper.insertFcmToken(accountId, "device-2", "fcm-token-2", true);

            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("device-fid", "device-1")
                    .when().post("/api/v1/auth/logout")
                    .then().log().all()
                    .statusCode(204);

            Long remaining = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM fcm_token WHERE account_id = ? AND device_fid = ?",
                    Long.class, accountId, "device-2");
            assertThat(remaining).isEqualTo(1);
        }
    }
}
