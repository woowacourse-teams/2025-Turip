package turip.region.api;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

import io.restassured.RestAssured;
import java.util.List;
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
import turip.infrastructure.client.KoreaTourismRelatedSpotClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.infrastructure.client.dto.RelatedSpotResult;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RelatedSpotApiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TestDataHelper testDataHelper;

    @MockitoBean
    private KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    @BeforeEach
    void setUp() {
        testDataHelper.cleanDatabase();
    }

    @DisplayName("/api/v1/related-spots GET 연관 관광지 조회 테스트")
    @Nested
    class ReadRelatedSpots {

        @DisplayName("한글 지역 카테고리로 연관 관광지 조회 성공 시 200 OK 코드와 연관 관광지 목록을 응답한다")
        @Test
        void readRelatedSpots1() {
            // given
            RelatedSpot spot1 = createRelatedSpot("북촌한옥마을", "관광지");
            RelatedSpot spot2 = createRelatedSpot("인사동", "관광지");
            RelatedSpot spot3 = createRelatedSpot("종로맛집", "음식점");

            // 서울의 3개 시군구 코드에 대해 모킹
            given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110))
                    .willReturn(RelatedSpotResult.success(List.of(spot1)));
            given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11440))
                    .willReturn(RelatedSpotResult.success(List.of(spot2)));
            given(koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11140))
                    .willReturn(RelatedSpotResult.success(List.of(spot3)));

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "서울")
                    .when().get("/api/v1/related-spots")
                    .then()
                    .statusCode(200)
                    .body("relatedSpots.size()", is(2)) // 관광지, 음식점
                    .body("relatedSpots[0].category", is("관광지"))
                    .body("relatedSpots[0].spots.size()", is(2))
                    .body("relatedSpots[1].category", is("음식점"))
                    .body("relatedSpots[1].spots.size()", is(1));
        }

        @DisplayName("외부 API에서 빈 응답이 오면 200 OK 코드와 튜립에서 수집한 장소를 리턴한다")
        @Test
        void readRelatedSpots2() {
            // given
            given(koreaTourismRelatedSpotClient.searchRelatedSpots(anyInt(), anyInt()))
                    .willReturn(RelatedSpotResult.failure());

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "부산")
                    .when().get("/api/v1/related-spots")
                    .then()
                    .statusCode(200)
                    .body("relatedSpots.size()", greaterThan(0));
        }

        @DisplayName("지원하지 않는 지역 카테고리로 조회 시 400 Bad Request를 응답한다")
        @Test
        void readRelatedSpots3() {
            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "존재하지않는지역")
                    .when().get("/api/v1/related-spots")
                    .then()
                    .statusCode(400);
        }

        @DisplayName("'국내 기타' 카테고리로 조회 시 400 Bad Request를 응답한다")
        @Test
        void readRelatedSpots4() {
            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "국내 기타")
                    .when().get("/api/v1/related-spots")
                    .then()
                    .statusCode(400);
        }

        private RelatedSpot createRelatedSpot(String relatedSpotName, String relatedCategoryLargeName) {
            RelatedSpot spot = new RelatedSpot();
            try {
                java.lang.reflect.Field relatedSpotField = RelatedSpot.class.getDeclaredField("relatedSpotName");
                relatedSpotField.setAccessible(true);
                relatedSpotField.set(spot, relatedSpotName);

                java.lang.reflect.Field categoryField = RelatedSpot.class.getDeclaredField("relatedCategoryLargeName");
                categoryField.setAccessible(true);
                categoryField.set(spot, relatedCategoryLargeName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return spot;
        }
    }
}
