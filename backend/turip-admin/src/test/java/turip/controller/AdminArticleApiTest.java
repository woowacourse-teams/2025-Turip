package turip.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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

    @Nested
    @DisplayName("/api/v1/admin/articles GET 아티클 목록 조회 테스트")
    class FindArticlesTest {

        @Test
        @DisplayName("관리자가 아티클 목록을 조회하면 공개·비공개 상관없이 200 OK와 목록을 응답한다")
        void findArticles1() {
            // given
            String adminAccessToken = createAdminAccessToken();
            createArticle(adminAccessToken, true);
            createArticle(adminAccessToken, false);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/articles")
                    .then()
                    .statusCode(200)
                    .body("articles", hasSize(2));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 아티클 목록을 조회하면 403 Forbidden을 응답한다")
        void findArticles2() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/articles")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/articles/{id} GET 아티클 상세 조회 테스트")
    class GetArticleTest {

        @Test
        @DisplayName("관리자가 비공개 아티클을 상세 조회해도 200 OK와 상세 정보를 응답한다")
        void getArticle1() {
            // given
            String adminAccessToken = createAdminAccessToken();
            Long articleId = createArticle(adminAccessToken, false);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/articles/" + articleId)
                    .then()
                    .statusCode(200)
                    .body("id", notNullValue());
        }

        @Test
        @DisplayName("존재하지 않는 아티클을 조회하면 404 Not Found를 응답한다")
        void getArticle2() {
            // given
            String adminAccessToken = createAdminAccessToken();

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/articles/999")
                    .then()
                    .statusCode(404);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/articles/{id} PATCH 아티클 수정 테스트")
    class UpdateTest {

        @Test
        @DisplayName("관리자가 아티클을 수정하면 200 OK와 수정된 아티클을 응답한다")
        void update1() {
            // given
            String adminAccessToken = createAdminAccessToken();
            Long articleId = createArticle(adminAccessToken, false);

            Map<String, Object> request = Map.of(
                    "title", "새 제목",
                    "subtitle", "새 부제목",
                    "content", "새 본문",
                    "isPublished", true,
                    "tagNames", List.of("여행"),
                    "placeIds", List.of()
            );

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().patch("/api/v1/admin/articles/" + articleId)
                    .then()
                    .statusCode(200)
                    .body("title", is("새 제목"))
                    .body("isPublished", is(true))
                    .body("tags", hasSize(1));
        }

        @Test
        @DisplayName("존재하지 않는 아티클을 수정하면 404 Not Found를 응답한다")
        void update2() {
            // given
            String adminAccessToken = createAdminAccessToken();

            Map<String, Object> request = Map.of(
                    "title", "새 제목",
                    "subtitle", "새 부제목",
                    "content", "새 본문",
                    "isPublished", true,
                    "tagNames", List.of(),
                    "placeIds", List.of()
            );

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().patch("/api/v1/admin/articles/999")
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 아티클을 수정하면 403 Forbidden을 응답한다")
        void update3() {
            // given
            String adminAccessToken = createAdminAccessToken();
            Long articleId = createArticle(adminAccessToken, false);
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            Map<String, Object> request = Map.of(
                    "title", "새 제목",
                    "subtitle", "새 부제목",
                    "content", "새 본문",
                    "isPublished", true,
                    "tagNames", List.of(),
                    "placeIds", List.of()
            );

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .contentType(ContentType.JSON)
                    .body(request)
                    .when().patch("/api/v1/admin/articles/" + articleId)
                    .then()
                    .statusCode(403);
        }
    }

    private String createAdminAccessToken() {
        Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
        testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
        return testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);
    }

    private Long createArticle(String adminAccessToken, boolean isPublished) {
        Map<String, Object> request = Map.of(
                "title", "제목",
                "subtitle", "부제목",
                "content", "본문",
                "isPublished", isPublished,
                "tagNames", List.of(),
                "placeIds", List.of()
        );

        return RestAssured.given().port(port)
                .header("Authorization", "Bearer " + adminAccessToken)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/api/v1/admin/articles")
                .then()
                .extract().as(Long.class);
    }
}
