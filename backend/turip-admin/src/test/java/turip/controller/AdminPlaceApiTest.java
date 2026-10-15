package turip.controller;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Role;
import turip.container.TestContainerConfig;
import turip.controller.dto.response.PlaceSuggestionResponse;
import turip.controller.dto.response.PlaceSuggestionResponse.PlaceSuggestionItem;
import turip.service.AdminPlaceSuggestionService;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "test-mysql"})
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = turip.TuripAdminApplication.class
)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class AdminPlaceApiTest extends TestContainerConfig {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoBean
    private AdminPlaceSuggestionService adminPlaceSuggestionService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;

        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0");
        jdbcTemplate.execute("TRUNCATE TABLE place_category");
        jdbcTemplate.execute("TRUNCATE TABLE article_place");
        jdbcTemplate.execute("TRUNCATE TABLE place");
        jdbcTemplate.execute("TRUNCATE TABLE refresh_token");
        jdbcTemplate.execute("TRUNCATE TABLE turip_member");
        jdbcTemplate.execute("TRUNCATE TABLE member");
        jdbcTemplate.execute("TRUNCATE TABLE account");
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1");
    }

    private void insertPlace(String name) {
        jdbcTemplate.update(
                "INSERT INTO place (name, url, address, latitude, longitude) VALUES (?, ?, '주소', 37.5, 127.0)",
                name, "https://place.example.com/" + name + System.nanoTime());
    }

    private String createAdminAccessToken() {
        Long adminAccountId = testDataHelper.insertAccount(Role.ADMIN);
        testDataHelper.insertTuripMember(adminAccountId, "admin@turip.com", false, "admin", "password123!");
        return testDataHelper.createAccessToken(adminAccountId, Role.ADMIN);
    }

    @Nested
    @DisplayName("/api/v1/admin/places GET 아티클 연관 장소 조회 테스트")
    class FindPlacesTest {

        @Test
        @DisplayName("query 없이 조회하면 전체 장소를 200 OK로 응답한다")
        void findPlaces1() {
            // given
            String adminAccessToken = createAdminAccessToken();
            insertPlace("경복궁");
            insertPlace("남산타워");

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .when().get("/api/v1/admin/places")
                    .then()
                    .statusCode(200)
                    .body("size()", is(2));
        }

        @Test
        @DisplayName("query로 조회하면 이름이 일치하는 장소만 200 OK로 응답한다")
        void findPlaces2() {
            // given
            String adminAccessToken = createAdminAccessToken();
            insertPlace("경복궁");
            insertPlace("남산타워");

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + adminAccessToken)
                    .queryParam("query", "경복궁")
                    .when().get("/api/v1/admin/places")
                    .then()
                    .statusCode(200)
                    .body("size()", is(1))
                    .body("[0].name", is("경복궁"));
        }

        @Test
        @DisplayName("관리자가 아닌 사용자가 조회하면 403 Forbidden을 응답한다")
        void findPlaces3() {
            // given
            Long userAccountId = testDataHelper.insertAccount(Role.USER);
            String userAccessToken = testDataHelper.createAccessToken(userAccountId, Role.USER);

            // when & then
            RestAssured.given().port(port)
                    .header("Authorization", "Bearer " + userAccessToken)
                    .when().get("/api/v1/admin/places")
                    .then()
                    .statusCode(403);
        }
    }

    @Nested
    @DisplayName("/api/v1/admin/places/suggestions GET 영상 장소 추천 조회 테스트")
    class GetPlaceSuggestionsTest {

        @Test
        @DisplayName("추천 리스트를 모델명, 토큰 수와 함께 200 OK로 응답한다")
        void getPlaceSuggestions_returnsSuggestionList() {
            // given
            PlaceSuggestionResponse response = new PlaceSuggestionResponse(
                    "gemini-3.8-flash", 1234,
                    List.of(new PlaceSuggestionItem("해운대", 95, 330, "자막 확인"))
            );
            when(adminPlaceSuggestionService.getSuggestions(anyString())).thenReturn(response);

            // when & then
            RestAssured.given().port(port)
                    .queryParam("url", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                    .when().get("/api/v1/admin/places/suggestions")
                    .then()
                    .statusCode(200)
                    .body("modelName", is("gemini-3.8-flash"))
                    .body("totalTokenCount", is(1234))
                    .body("suggestions[0].placeName", is("해운대"))
                    .body("suggestions[0].confidence", is(95))
                    .body("suggestions[0].timeSeconds", is(330));
        }

        @Test
        @DisplayName("Gemini가 전부 실패해도 200과 빈 추천 리스트를 응답한다")
        void getPlaceSuggestions_whenAllModelsFail_returnsEmptyList() {
            // given
            PlaceSuggestionResponse response = new PlaceSuggestionResponse("gemini-3.5-flash-lite", null, List.of());
            when(adminPlaceSuggestionService.getSuggestions(anyString())).thenReturn(response);

            // when & then
            RestAssured.given().port(port)
                    .queryParam("url", "https://www.youtube.com/watch?v=dQw4w9WgXcQ")
                    .when().get("/api/v1/admin/places/suggestions")
                    .then()
                    .statusCode(200)
                    .body("suggestions.size()", is(0));
        }
    }
}
