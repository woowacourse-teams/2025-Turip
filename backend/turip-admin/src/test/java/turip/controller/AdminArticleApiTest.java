package turip.controller;

import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import turip.account.domain.Role;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
class AdminArticleApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("/api/v1/admin/articles POST 아티클 생성 테스트")
    class CreateTest {

        @Test
        @DisplayName("관리자가 아티클을 생성하면 201 Created와 생성된 아티클 id를 응답한다")
        void create1() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Map<String, Object> request = Map.of(
                    "title", "제목",
                    "subtitle", "부제목",
                    "content", "본문",
                    "isPublished", true,
                    "tagNames", List.of("여행", "행복"),
                    "placeIds", List.of()
            );

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/v1/admin/articles")
                    .then()
                    .statusCode(201)
                    .body(notNullValue());
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 아티클을 생성하면 403 Forbidden을 응답한다")
        void create2() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            Map<String, Object> request = Map.of(
                    "title", "제목",
                    "subtitle", "부제목",
                    "content", "본문",
                    "isPublished", false,
                    "tagNames", List.of(),
                    "placeIds", List.of()
            );

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().post("/api/v1/admin/articles")
                    .then()
                    .statusCode(403);
        }
    }
}
