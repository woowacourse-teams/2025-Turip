package turip.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import io.restassured.RestAssured;
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
import turip.content.domain.ContentPending;
import turip.content.domain.ContentPendingData;
import turip.content.repository.ContentPendingRepository;
import turip.util.fixture.AccountFixture;
import turip.util.fixture.ContentPendingFixture;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
class AdminContentApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestDataHelper testDataHelper;

    @Autowired
    private ContentPendingRepository contentPendingRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        testDataHelper.cleanDatabase();
    }

    @Nested
    @DisplayName("/api/v1/admin/contents/my GET 나의 콘텐츠 수집 내역 조회 테스트")
    class GetMyHistoryTest {

        @Test
        @DisplayName("관리자가 자신의 콘텐츠 수집 내역을 조회하면 200 OK와 수집 내역 리스트를 응답한다")
        void getMyHistory_Success() {
            // given
            Account collectorAccount = AccountFixture.createCustomAccount(1L, Role.ADMIN);
            Long collectorAccountId = testDataHelper.insertAccount(collectorAccount);
            ReflectionTestUtils.setField(collectorAccount, "id", collectorAccountId);

            testDataHelper.insertTuripMember(collectorAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(collectorAccountId, Role.ADMIN);

            ContentPendingData contentData1 = ContentPendingFixture.createTestContentData("서울");
            ContentPending contentPending1 = new ContentPending(contentData1, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending1);

            ContentPendingData contentData2 = ContentPendingFixture.createTestContentData("부산");
            ContentPending contentPending2 = new ContentPending(contentData2, collectorAccount, null, null, null);
            contentPendingRepository.save(contentPending2);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/contents/my")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(2))
                    .body("[0].id", notNullValue())
                    .body("[0].videoTitle", is("테스트 비디오"))
                    .body("[0].cityName", is("부산"))
                    .body("[0].status", is("PENDING"))
                    .body("[0].rejectReason", nullValue())
                    .body("[1].id", notNullValue())
                    .body("[1].videoTitle", is("테스트 비디오"))
                    .body("[1].cityName", is("서울"))
                    .body("[1].status", is("PENDING"));
        }

        @Test
        @DisplayName("관리자가 수집 내역이 없는 경우 빈 리스트를 응답한다")
        void getMyHistory_EmptyList() {
            // given
            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/contents/my")
                    .then()
                    .statusCode(200)
                    .body("$", hasSize(0));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 수집 내역을 조회하면 403 Forbidden을 응답한다")
        void getMyHistory_Forbidden() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/contents/my")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/contents GET 콘텐츠 검색/목록 조회 테스트")
    class FindContentsTest {

        @Test
        @DisplayName("keyword 없이 조회하면 전체 콘텐츠를 id 내림차순으로 응답한다")
        void findContents1() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO creator (profile_image, channel_name) VALUES (?, ?)",
                    "https://image.example.com/creator1.jpg", "TravelMate");
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://image.example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://image.example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd1", "서울 데이트 코스 추천", "2024-07-01");
            jdbcTemplate.update(
                    "INSERT INTO content (creator_id, city_id, url, title, uploaded_date) VALUES (?, ?, ?, ?, ?)",
                    1, 1, "https://youtube.com/watch?v=abcd2", "서울 맛집 투어", "2024-08-01");

            Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
            testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
            String adminAccessToken = testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/contents?size=10&lastId=0")
                    .then()
                    .statusCode(200)
                    .body("contents", hasSize(2))
                    .body("contents[0].id", is(2))
                    .body("contents[0].title", is("서울 맛집 투어"))
                    .body("contents[1].id", is(1))
                    .body("loadable", is(false));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 조회하면 403 Forbidden을 응답한다")
        void findContents2() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/contents?size=10&lastId=0")
                    .then()
                    .statusCode(403);
        }
    }
}
