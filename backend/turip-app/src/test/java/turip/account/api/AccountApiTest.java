package turip.account.api;

import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import turip.account.domain.Provider;
import turip.account.domain.Role;
import turip.auth.token.GoogleTokenParser;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoBean
    private GoogleTokenParser googleTokenParser;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("GET /api/v1/accounts/me 내 계정 정보 조회 테스트")
    class GetMeTest {

        @Test
        @DisplayName("유효한 access token으로 요청 시 200 OK와 계정 정보를 응답한다")
        void getMeSuccess() {
            // given
            Long accountId = testDataHelper.insertAccount(Role.USER);
            String email = "test@gmail.com";
            Provider provider = Provider.GOOGLE;
            String providerId = "google-user-123";

            Long memberId = testDataHelper.insertMember(accountId, email, true);
            testDataHelper.insertSocialMember(memberId, provider, providerId);

            String accessToken = testDataHelper.createAccessToken(accountId);

            // when & then
            RestAssured
                    .given().log().all()
                    .header("Authorization", "Bearer " + accessToken)
                    .when().get("/api/v1/accounts/me")
                    .then().log().all()
                    .statusCode(200)
                    .body("id", notNullValue())
                    .body("nickname", notNullValue())
                    .body("role", notNullValue());
        }
    }
}
