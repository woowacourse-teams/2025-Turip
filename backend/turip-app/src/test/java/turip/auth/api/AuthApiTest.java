package turip.auth.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static turip.common.exception.ErrorTag.ACCOUNT_CREATION_ERROR;
import static turip.common.exception.ErrorTag.ID_TOKEN_NOT_VALID;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.account.service.AccountService;
import turip.auth.token.AppleTokenParser;
import turip.auth.token.GoogleTokenParser;
import turip.common.exception.custom.InternalServerException;
import turip.common.exception.custom.UnauthorizedException;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthApiTest {

    private static GoogleTokenParser googleTokenParserMock;
    private static AppleTokenParser appleTokenParserMock;

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public GoogleTokenParser googleTokenParser() {
            googleTokenParserMock = Mockito.mock(GoogleTokenParser.class);
            Mockito.when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
            return googleTokenParserMock;
        }

        @Bean
        @Primary
        public AppleTokenParser appleTokenParser() {
            appleTokenParserMock = Mockito.mock(AppleTokenParser.class);
            Mockito.when(appleTokenParserMock.getProvider()).thenReturn(Provider.APPLE);
            return appleTokenParserMock;
        }
    }

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoSpyBean
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();

        // Mock 리셋
        Mockito.reset(googleTokenParserMock, appleTokenParserMock, accountService);
        Mockito.when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
        Mockito.when(appleTokenParserMock.getProvider()).thenReturn(Provider.APPLE);
    }

    @Nested
    @DisplayName("/api/v1/auth/login/turip POST 자체 로그인 테스트")
    class LoginWithTuripTest {

        @Test
        @DisplayName("튜립 회원 로그인 성공 시 200 Ok와 토큰을 응답한다")
        void loginWithTurip1() {
            // given
            String deviceFid = "device-123";
            String email = "turip@gmail.com";
            String loginId = "turip";
            String loginPassword = "ValidPass1!";
            Role role = Role.USER;

            Long accountId = testDataHelper.insertAccount(role);
            testDataHelper.insertTuripMember(accountId, email, true, loginId, loginPassword);

            Map<String, String> requestBody = new HashMap<>(Map.of("loginId", loginId, "loginPassword", loginPassword));

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/turip")
                    .then().log().all()
                    .statusCode(200)
                    .cookie("accessToken")
                    .cookie("refreshToken");
        }

        @Test
        @DisplayName("loginId에 대한 회원이 존재하지 않는 경우 401 Unauthorized를 응답한다")
        void loginWithTurip2() {
            // given
            String deviceFid = "device-123";
            String loginId = "turip";
            String loginPassword = "ValidPass1!";

            Map<String, String> requestBody = new HashMap<>(Map.of("loginId", loginId, "loginPassword", loginPassword));

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/turip")
                    .then().log().all()
                    .statusCode(401);
        }

        @Test
        @DisplayName("비밀번호가 일치하지 않는 경우 401 Unauthorized를 응답한다")
        void loginWithTurip3() {
            // given
            String deviceFid = "device-123";
            String email = "turip@gmail.com";
            String loginId = "turip";
            String loginPassword = "InvalidPass1!";
            String realPassword = "ValidPass1!";
            boolean isFirstLogin = true;

            testDataHelper.insertTuripMember(email, isFirstLogin, loginId, realPassword);

            Map<String, String> requestBody = new HashMap<>(Map.of("loginId", loginId, "loginPassword", loginPassword));

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/turip")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("/api/v1/auth/login/google POST 구글 로그인 테스트")
    class LoginWithGoogleTest {

        @Test
        @DisplayName("신규 소셜 회원 로그인 성공 시 200 Ok와 토큰을 응답한다")
        void loginNewMemberSuccess() {
            // given
            String idToken = "valid-google-id-token";
            String deviceFid = "device-123";

            when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn("google-user-123");
            when(googleTokenParserMock.getEmail(idToken)).thenReturn("newuser@gmail.com");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("isNewMember", is(true));
        }

        @Test
        @DisplayName("기존 소셜 회원 로그인 성공 시 200 OK와 토큰을 응답한다")
        void loginExistingMemberSuccess() {
            // given
            String email = "existing@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-existing";
            testDataHelper.insertSocialMember(email, false, provider, providerId);

            String idToken = "valid-google-id-token";
            String deviceFid = "device-456";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/google")
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

            when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParserMock.getProviderId(anyString()))
                    .thenThrow(new UnauthorizedException(
                            ID_TOKEN_NOT_VALID));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", invalidIdToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/google")
                    .then().log().all()
                    .statusCode(401)
                    .body("tag", is("ID_TOKEN_NOT_VALID"));
        }

        @Test
        @DisplayName("계정 생성에 실패한 경우 500 Internal Server Error를 응답한다")
        void accountCreationFailed() {
            // given
            String email = "newuser@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-new";

            String idToken = "valid-google-id-token";
            String deviceFid = "device-456";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);
            doThrow(new InternalServerException(ACCOUNT_CREATION_ERROR))
                    .when(accountService)
                    .create(any());
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/google")
                    .then().log().all()
                    .statusCode(500)
                    .body("tag", is("ACCOUNT_CREATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("/api/v2/auth/login/google POST 구글 로그인 V2 테스트")
    class LoginWithGoogleV2Test {

        @Test
        @DisplayName("신규 소셜 회원 로그인 성공 시 200 Ok와 토큰을 응답하고, 마이그레이션 여부를 false로 응답한다.")
        void loginNewMemberSuccess() {
            // given
            String idToken = "valid-google-id-token";
            String deviceFid = "device-123";

            when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn("google-user-123");
            when(googleTokenParserMock.getEmail(idToken)).thenReturn("newuser@gmail.com");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v2/auth/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("isMigrationDecided", is(false));
        }

        @Test
        @DisplayName("유효하지 않은 ID 토큰인 경우 401 Unauthorized를 응답한다")
        void loginWithInvalidIdToken() {
            // given
            String invalidIdToken = "invalid-token";
            String deviceFid = "device-123";

            when(googleTokenParserMock.getProvider()).thenReturn(Provider.GOOGLE);
            when(googleTokenParserMock.getProviderId(anyString()))
                    .thenThrow(new UnauthorizedException(ID_TOKEN_NOT_VALID));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", invalidIdToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v2/auth/login/google")
                    .then().log().all()
                    .statusCode(401)
                    .body("tag", is("ID_TOKEN_NOT_VALID"));
        }

        @Test
        @DisplayName("계정 생성에 실패한 경우 500 Internal Server Error를 응답한다")
        void accountCreationFailed() {
            // given
            String email = "newuser@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-new";

            String idToken = "valid-google-id-token";
            String deviceFid = "device-456";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);
            doThrow(new InternalServerException(ACCOUNT_CREATION_ERROR))
                    .when(accountService)
                    .create(any());
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v2/auth/login/google")
                    .then().log().all()
                    .statusCode(500)
                    .body("tag", is("ACCOUNT_CREATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("/api/v1/auth/login/apple POST 애플 로그인 테스트")
    class LoginWithAppleTest {

        @Test
        @DisplayName("신규 소셜 회원 로그인 성공 시 200 Ok와 토큰을 응답한다")
        void loginNewMemberSuccess() {
            // given
            String idToken = "valid-apple-id-token";
            String deviceFid = "device-123";

            when(appleTokenParserMock.getProviderId(idToken)).thenReturn("apple-user-123");
            when(appleTokenParserMock.getEmail(idToken)).thenReturn("newuser@icloud.com");

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/apple")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("nickname", notNullValue())
                    .body("isMigrationDecided", is(false));
        }

        @Test
        @DisplayName("기존 소셜 회원 로그인 성공 시 200 OK와 토큰을 응답한다")
        void loginExistingMemberSuccess() {
            // given
            String email = "existing@icloud.com";
            Provider provider = Provider.APPLE;
            String providerId = "apple-user-existing";
            testDataHelper.insertSocialMember(email, false, provider, providerId);

            String idToken = "valid-apple-id-token";
            String deviceFid = "device-456";

            when(appleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(appleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/apple")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("nickname", notNullValue())
                    .body("isMigrationDecided", is(false));
        }

        @Test
        @DisplayName("애플 재로그인 시 email이 없어도 로그인에 성공한다")
        void loginExistingMemberWithoutEmail() {
            // given - 첫 로그인 시 email로 회원가입된 상태
            String email = "existing@icloud.com";
            Provider provider = Provider.APPLE;
            String providerId = "apple-user-relogin";
            testDataHelper.insertSocialMember(email, false, provider, providerId);

            String idToken = "valid-apple-id-token-relogin";
            String deviceFid = "device-relogin-456";

            // 애플 재로그인 시에는 email이 null
            when(appleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(appleTokenParserMock.getEmail(idToken)).thenReturn(null);

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/apple")
                    .then().log().all()
                    .statusCode(200)
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue())
                    .body("nickname", notNullValue())
                    .body("isMigrationDecided", is(false));
        }

        @Test
        @DisplayName("유효하지 않은 ID 토큰인 경우 401 Unauthorized를 응답한다")
        void loginWithInvalidIdToken() {
            // given
            String invalidIdToken = "invalid-token";
            String deviceFid = "device-123";

            when(appleTokenParserMock.getProviderId(anyString()))
                    .thenThrow(new UnauthorizedException(
                            ID_TOKEN_NOT_VALID));

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", invalidIdToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/apple")
                    .then().log().all()
                    .statusCode(401)
                    .body("tag", is("ID_TOKEN_NOT_VALID"));
        }

        @Test
        @DisplayName("계정 생성에 실패한 경우 500 Internal Server Error를 응답한다")
        void accountCreationFailed() {
            // given
            String email = "newuser@icloud.com";
            String providerId = "apple-user-new";

            String idToken = "valid-apple-id-token";
            String deviceFid = "device-456";

            when(appleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(appleTokenParserMock.getEmail(idToken)).thenReturn(email);
            doThrow(new InternalServerException(ACCOUNT_CREATION_ERROR))
                    .when(accountService)
                    .create(any());
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("idToken", idToken);

            // when & then
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(requestBody)
                    .when().post("/api/v1/auth/login/apple")
                    .then().log().all()
                    .statusCode(500)
                    .body("tag", is("ACCOUNT_CREATION_ERROR"));
        }
    }

    @Nested
    @DisplayName("/api/v1/auth/tokens POST 토큰 갱신 테스트")
    class RefreshTest {

        @Test
        @DisplayName("유효한 refresh token으로 새로운 토큰을 발급받는다")
        void refreshTokenSuccess() {
            // given
            // 1. 먼저 로그인해서 토큰 받기
            String email = "refresh@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-refresh";
            testDataHelper.insertSocialMember(email, true, provider, providerId);

            String idToken = "valid-google-id-token";
            String deviceFid = "device-refresh-123";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            String refreshToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/google")
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
                    .when().post("/api/v1/auth/tokens")
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
                    .when().post("/api/v1/auth/tokens")
                    .then().log().all()
                    .statusCode(401);
        }

        @Test
        @DisplayName("DB에 저장된 토큰과 일치하지 않으면 401 Unauthorized를 응답한다")
        void refreshTokenMismatch() {
            // given
            String email = "mismatch@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-mismatch";
            testDataHelper.insertSocialMember(email, true, provider, providerId);

            String idToken = "valid-google-id-token";
            String deviceFid = "device-mismatch-123";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/google")
                    .then().log().all()
                    .statusCode(200);

            // 다른 사용자의 refresh token 사용 시도
            String email2 = "other@gmail.com";
            String providerId2 = "google-user-other";
            testDataHelper.insertSocialMember(email2, true, provider, providerId2);

            String otherIdToken = "other-valid-google-id-token";
            String otherDeviceFid = "device-other-123";

            when(googleTokenParserMock.getProviderId(otherIdToken)).thenReturn(providerId2);
            when(googleTokenParserMock.getEmail(otherIdToken)).thenReturn(email2);

            Map<String, String> otherLoginRequest = new HashMap<>();
            otherLoginRequest.put("idToken", otherIdToken);

            String otherRefreshToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", otherDeviceFid)
                    .body(otherLoginRequest)
                    .when().post("/api/v1/auth/login/google")
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
                    .when().post("/api/v1/auth/tokens")
                    .then().log().all()
                    .statusCode(401);
        }
    }

    @Nested
    @DisplayName("/api/v1/auth/logout POST 로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("정상적으로 로그아웃에 성공하면 204 No Content를 응답한다")
        void logoutSuccess() {
            // given
            String email = "logout@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-logout";
            testDataHelper.insertSocialMember(email, true, provider, providerId);

            String idToken = "valid-google-id-token";
            String deviceFid = "device-logout-123";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            String accessToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/google")
                    .then().log().all()
                    .statusCode(200)
                    .extract().path("accessToken");

            // when & then
            RestAssured
                    .given().log().all()
                    .header("device-fid", deviceFid)
                    .header("Authorization", "Bearer " + accessToken)
                    .when().post("/api/v1/auth/logout")
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        @DisplayName("로그아웃 후 refresh token이 DB에서 삭제된다")
        void logoutDeletesRefreshToken() {
            // given
            String email = "logout-delete@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-logout-delete";

            testDataHelper.insertSocialMember(email, true, provider, providerId);

            String idToken = "valid-google-id-token";
            String deviceFid = "device-logout-delete-123";

            when(googleTokenParserMock.getProvider()).thenReturn(provider);
            when(googleTokenParserMock.getProviderId(idToken)).thenReturn(providerId);
            when(googleTokenParserMock.getEmail(idToken)).thenReturn(email);

            Map<String, String> loginRequest = new HashMap<>();
            loginRequest.put("idToken", idToken);

            // 로그인해서 토큰 받기
            String accessToken = RestAssured
                    .given().log().all()
                    .contentType(ContentType.JSON)
                    .header("device-fid", deviceFid)
                    .body(loginRequest)
                    .when().post("/api/v1/auth/login/google")
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
                    .when().post("/api/v1/auth/logout")
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
                    .when().post("/api/v1/auth/logout")
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
                    .when().post("/api/v1/auth/logout")
                    .then().log().all()
                    .statusCode(401);
        }
    }
}
