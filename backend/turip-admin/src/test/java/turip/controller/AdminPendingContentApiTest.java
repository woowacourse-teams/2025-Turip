package turip.controller;

import static org.hamcrest.Matchers.hasSize;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;
import turip.account.domain.Account;
import turip.account.domain.Role;
import turip.container.TestContainerConfig;
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.repository.ContentPendingRepository;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ContentPendingFixture;
import turip.util.helper.TestDataHelper;

@ActiveProfiles("test-mysql")
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
class AdminPendingContentApiTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ContentPendingRepository contentPendingRepository;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");

        jdbcTemplate.execute("TRUNCATE TABLE place_category");
        jdbcTemplate.execute("TRUNCATE TABLE content_place");
        jdbcTemplate.execute("TRUNCATE TABLE place");
        jdbcTemplate.execute("TRUNCATE TABLE category");
        jdbcTemplate.execute("TRUNCATE TABLE favorite_folder_account");
        jdbcTemplate.execute("TRUNCATE TABLE favorite_folder");
        jdbcTemplate.execute("TRUNCATE TABLE favorite_content");
        jdbcTemplate.execute("TRUNCATE TABLE content_pending");
        jdbcTemplate.execute("TRUNCATE TABLE guest");
        jdbcTemplate.execute("TRUNCATE TABLE turip_member");
        jdbcTemplate.execute("TRUNCATE TABLE social_member");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("TRUNCATE TABLE account");
        jdbcTemplate.execute("TRUNCATE TABLE content");
        jdbcTemplate.execute("TRUNCATE TABLE creator");
        jdbcTemplate.execute("TRUNCATE TABLE city");
        jdbcTemplate.execute("TRUNCATE TABLE country");
        jdbcTemplate.execute("TRUNCATE TABLE province");

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
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

        @Test
        @DisplayName("자신이 수집한 콘텐츠를 승인하려 할 때 400 Bad Request를 응답한다")
        void approveContentPending_ValidatorNotValid() {
            // given
            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            testDataHelper.insertTuripMember(collectorAccountId, "admin@turip.com", false, "admin", "password123!");
            String collectorAccessToken = testDataHelper.createAccessToken(collectorAccountId, Role.ADMIN);

            testDataHelper.insertCity("서울");
            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + collectorAccessToken)
                    .when().post("/api/v1/admin/content-pendings/{id}/approve", 1)
                    .then()
                    .statusCode(400);
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

    @DisplayName("/api/v1/admin/content-pendings POST 펜딩 콘텐츠 등록 테스트")
    @Nested
    class PendingContentTest {

        @DisplayName("관리자가 콘텐츠를 pending 상태로 등록하면 201 CREATED와 contentPendingId를 응답한다")
        @Test
        void pending_Success() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // validator로 지정될 다른 admin 계정 생성
            Long validatorAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(validatorAccountId, "validator@turip.com", false, "validator", "password123!");

            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .body(contentData)
                    .when().post("/api/v1/admin/content-pendings")
                    .then()
                    .log().all()
                    .statusCode(201)
                    .body(notNullValue());
        }

        @DisplayName("관리자가 아닌 사용자가 콘텐츠를 등록하려 하면 403 Forbidden을 응답한다")
        @Test
        void pending_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .body(contentData)
                    .when().post("/api/v1/admin/content-pendings")
                    .then()
                    .statusCode(403);
        }

        @DisplayName("이미 PENDING 상태인 동일 영상을 등록하려 하면 409 Conflict를 응답한다")
        @Test
        void pending_Conflict() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // validator로 지정될 다른 admin 계정 생성
            Long validatorAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(validatorAccountId, "validator@turip.com", false, "validator", "password123!");

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            ContentPendingData contentData = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending = new ContentPending(contentData, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending);

            // when & then
            RestAssured.given().port(port)
                    .contentType(ContentType.JSON)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .body(contentData)
                    .when().post("/api/v1/admin/content-pendings")
                    .then()
                    .statusCode(409);
        }
    }

    @DisplayName("/api/v1/admin/content-pendings/list GET 펜딩 콘텐츠 목록 조회 테스트")
    @Nested
    class FindAllContentPendingTest {

        @DisplayName("관리자가 펜딩 콘텐츠 목록을 조회하면 200 OK와 PENDING 상태의 콘텐츠 리스트를 응답한다")
        @Test
        void findAll_Success() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount1 = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId1 = testDataHelper.insertAccount(collectorAccount1);
            ReflectionTestUtils.setField(collectorAccount1, "id", collectorAccountId1);

            Account collectorAccount2 = AccountFixture.createCustomAccount(2L, Role.ADMIN);
            Long collectorAccountId2 = testDataHelper.insertAccount(collectorAccount2);
            ReflectionTestUtils.setField(collectorAccount2, "id", collectorAccountId2);

            ContentPendingData contentData1 = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending1 = new ContentPending(contentData1, collectorAccount1, null, null, null);
            contentPendingRepository.save(contentPending1);

            ContentPendingData contentData2 = ContentPendingFixture.createTestContentData("부산");
            ContentPending contentPending2 = new ContentPending(contentData2, collectorAccount2, null, null, null);
            contentPendingRepository.save(contentPending2);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/content-pendings/list")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].id", notNullValue())
                    .body("[0].videoTitle", is("테스트 비디오"))
                    .body("[0].channelName", is("테스트 채널"))
                    .body("[0].collectorNickname", notNullValue())
                    .body("[0].createdAt", notNullValue())
                    .body("[1].id", notNullValue())
                    .body("[1].videoTitle", is("테스트 비디오"))
                    .body("[1].channelName", is("테스트 채널"));
        }

        @DisplayName("펜딩 콘텐츠가 없는 경우 빈 리스트를 응답한다")
        @Test
        void findAll_EmptyList() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/content-pendings/list")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }

        @DisplayName("PENDING 상태가 아닌 콘텐츠는 목록에 포함되지 않는다")
        @Test
        void findAll_OnlyPendingStatus() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            // PENDING 콘텐츠
            ContentPendingData contentData1 = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending1 = new ContentPending(contentData1, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending1);

            // REJECTED 콘텐츠
            ContentPendingData contentData2 = ContentPendingFixture.createTestContentData("부산");
            ContentPending contentPending2 = new ContentPending(contentData2, collectorAccount, null, null, null);
            contentPending2.reject(collectorAccount, "거절 사유");
            contentPendingRepository.save(contentPending2);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/content-pendings/list")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(1))
                    .body("[0].id", is(1));
        }

        @DisplayName("관리자가 아닌 사용자가 목록을 조회하면 403 Forbidden을 응답한다")
        @Test
        void findAll_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/content-pendings/list")
                    .then()
                    .statusCode(403);
        }
    }
}
