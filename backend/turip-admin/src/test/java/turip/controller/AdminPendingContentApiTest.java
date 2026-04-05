package turip.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.repository.ContentPendingRepository;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ContentPendingFixture;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
class AdminPendingContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ContentPendingRepository contentPendingRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("/api/v1/admin/content-pendings/{id} GET 특정 펜딩 콘텐츠 상세 조회 테스트")
    class FindContentPendingByIdTest {

        @Test
        @DisplayName("관리자가 존재하는 펜딩 콘텐츠를 조회하면 200 OK와 상세 정보를 응답한다")
        void findContentPendingById_Success() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/content-pendings/{id}", 1)
                    .then()
                    .statusCode(200)
                    .body("id", is(1))
                    .body("status", is("PENDING"))
                    .body("contentData", notNullValue())
                    .body("collectorAccount", notNullValue())
                    .body("collectorAccount.id", is(collectorAccountId.intValue()));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 펜딩 콘텐츠를 조회하면 403 Forbidden을 응답한다")
        void findContentPendingById_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/content-pendings/{id}", 1)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("존재하지 않는 펜딩 콘텐츠 ID로 조회하면 404 Not Found를 응답한다")
        void findContentPendingById_NotFound() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/content-pendings/{id}", 999)
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/api/v1/admin/content-pendings/{id}/approve POST 펜딩 콘텐츠 승인 테스트")
    @Nested
    class ApproveContentPendingTest {

        @DisplayName("관리자가 존재하는 펜딩 콘텐츠를 승인하면 200 OK와 contentId, contentPendingId를 응답한다")
        @Test
        void approveContentPending_Success() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            testDataHelper.insertCity("서울");
            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().post("/api/v1/admin/content-pendings/{id}/approve", 1)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("contentId", notNullValue())
                    .body("contentPendingId", is(1));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 펜딩 콘텐츠를 승인하려 하면 403 Forbidden을 응답한다")
        void approveContentPending_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().post("/api/v1/admin/content-pendings/{id}/approve", 1)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("존재하지 않는 펜딩 콘텐츠 ID로 승인하려 하면 404 Not Found를 응답한다")
        void approveContentPending_NotFound() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().post("/api/v1/admin/content-pendings/{id}/approve", 999)
                    .then()
                    .statusCode(404);
        }
    }

    @DisplayName("/api/v1/admin/content-pendings/{id}/reject POST 펜딩 콘텐츠 거절 테스트")
    @Nested
    class RejectContentPendingTest {

        @DisplayName("관리자가 존재하는 펜딩 콘텐츠를 거절하면 200 OK와 contentPendingId를 응답한다")
        @Test
        void rejectContentPending_Success() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            testDataHelper.insertCity("서울");
            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            ContentPendingRejectRequest request = new ContentPendingRejectRequest("거절 사유");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .body(request)
                    .when().post("/api/v1/admin/content-pendings/{id}/reject", 1)
                    .then()
                    .log().all()
                    .statusCode(200)
                    .body("contentPendingId", is(1));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 펜딩 콘텐츠를 거절하려 하면 403 Forbidden을 응답한다")
        void rejectContentPending_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);
            ContentPendingRejectRequest request = new ContentPendingRejectRequest("거절 사유");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .body(request)
                    .when().post("/api/v1/admin/content-pendings/{id}/reject", 1)
                    .then()
                    .statusCode(403);
        }

        @Test
        @DisplayName("존재하지 않는 펜딩 콘텐츠 ID로 거절하려 하면 404 Not Found를 응답한다")
        void rejectContentPending_NotFound() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);
            ContentPendingRejectRequest request = new ContentPendingRejectRequest("거절 사유");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .body(request)
                    .when().post("/api/v1/admin/content-pendings/{id}/reject", 999)
                    .then()
                    .statusCode(404);
        }

        @Test
        @DisplayName("거절 사유가 형식에 맞지 않는 경우 400 Bad Request를 응답한다")
        void rejectContentPending_BadRequest() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            testDataHelper.insertCity("서울");
            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            ContentPendingRejectRequest request = new ContentPendingRejectRequest(" ");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .body(request)
                    .when().post("/api/v1/admin/content-pendings/{id}/reject", 1)
                    .then()
                    .log().all()
                    .statusCode(400);
        }
    }
}
