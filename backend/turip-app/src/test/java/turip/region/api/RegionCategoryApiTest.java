package turip.region.api;

import static org.hamcrest.Matchers.is;
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
import turip.region.domain.TourApiAreaCode;
import turip.util.helper.TestDataHelper;

@ActiveProfiles({"test", "h2"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RegionCategoryApiTest {

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

    @DisplayName("/api/v1/region-categories GET 지역 카테고리 조회 테스트")
    @Nested
    class getRegionCategories {

        @DisplayName("국내 지역 카테고리 조회 성공 시 200 OK 코드와 국내 지역 카테고리 목록을 응답한다")
        @Test
        void getRegionCategoriesByCountryType_withIsKoreaTrue_returnsDomesticCategories() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, 'https://example.com/busan.jpg')");

            // creator 추가
            jdbcTemplate.update(
                    "INSERT INTO creator (channel_name, profile_image) VALUES ('테스트크리에이터', 'https://example.com/creator.jpg')");

            // 서울에 컨텐츠 3개, 부산에 컨텐츠 1개 추가
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠1', 'https://example.com/seoul1', '2024-01-01', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠2', 'https://example.com/seoul2', '2024-01-02', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠3', 'https://example.com/seoul3', '2024-01-03', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('부산컨텐츠1', 'https://example.com/busan1', '2024-01-04', 1, 2)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", true)
                    .when().get("/api/v1/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(2)) // 서울, 부산만 (국내 기타는 컨텐츠가 0개라서 제외)
                    .body("regionCategories[0].regionCategoryName", is("서울")) // 컨텐츠 3개
                    .body("regionCategories[1].regionCategoryName", is("부산")); // 컨텐츠 1개
        }

        @DisplayName("해외 지역 카테고리 조회 성공 시 200 OK 코드와 해외 지역 카테고리 목록을 응답한다")
        @Test
        void getRegionCategoriesByCountryType_withIsKoreaFalse_returnsOverseasCategories() {
            // given
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('일본', 'https://example.com/japan.jpg')");
            jdbcTemplate.update("INSERT INTO country (name, image_url) VALUES ('중국', 'https://example.com/china.jpg')");

            // 해외 지역을 위한 city 생성 (country_id 참조)
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('도쿄', 1, 'https://example.com/tokyo.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('베이징', 2, 'https://example.com/beijing.jpg')");

            // creator 추가
            jdbcTemplate.update(
                    "INSERT INTO creator (channel_name, profile_image) VALUES ('테스트크리에이터', 'https://example.com/creator.jpg')");

            // 일본에 컨텐츠 2개, 중국에 컨텐츠 1개 추가
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('일본컨텐츠1', 'https://example.com/japan1', '2024-01-01', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('일본컨텐츠2', 'https://example.com/japan2', '2024-01-02', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('중국컨텐츠1', 'https://example.com/china1', '2024-01-03', 1, 2)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", false)
                    .when().get("/api/v1/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(2)) // 일본, 중국만 (해외 기타는 컨텐츠가 0개라서 제외)
                    .body("regionCategories[0].regionCategoryName", is("일본")) // 컨텐츠 2개
                    .body("regionCategories[1].regionCategoryName", is("중국")); // 컨텐츠 1개
        }

        @DisplayName("컨텐츠가 없는 지역은 제외되고 반환된다")
        @Test
        void getRegionCategoriesByCountryType_withNoContent_returnsOnlyCategoriesWithContent() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, 'https://example.com/busan.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('대구', 1, 'https://example.com/daegu.jpg')");

            // creator 추가
            jdbcTemplate.update(
                    "INSERT INTO creator (channel_name, profile_image) VALUES ('테스트크리에이터', 'https://example.com/creator.jpg')");

            // 서울에만 컨텐츠 추가 (부산, 대구는 컨텐츠 없음)
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠1', 'https://example.com/seoul1', '2024-01-01', 1, 1)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", true)
                    .when().get("/api/v1/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(1)) // 서울만 (부산, 대구, 국내 기타는 컨텐츠가 없어서 제외)
                    .body("regionCategories[0].regionCategoryName", is("서울"));
        }

        @DisplayName("국내 기타 카테고리에 컨텐츠가 있으면 포함되어 반환된다")
        @Test
        void getRegionCategoriesByCountryType_withDomesticEtcContent_returnsDomesticEtcCategory() {
            // given
            jdbcTemplate.update(
                    "INSERT INTO country (name, image_url) VALUES ('대한민국', 'https://example.com/korea.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('서울', 1, 'https://example.com/seoul.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('부산', 1, 'https://example.com/busan.jpg')");

            // creator 추가
            jdbcTemplate.update(
                    "INSERT INTO creator (channel_name, profile_image) VALUES ('테스트크리에이터', 'https://example.com/creator.jpg')");

            // 서울에 컨텐츠 2개, 부산에 컨텐츠 1개 추가
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠1', 'https://example.com/seoul1', '2024-01-01', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('서울컨텐츠2', 'https://example.com/seoul2', '2024-01-02', 1, 1)");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('부산컨텐츠1', 'https://example.com/busan1', '2024-01-03', 1, 2)");

            // 국내 기타 지역에 컨텐츠 추가 (대구는 DomesticRegionCategory에 포함되지 않음)
            jdbcTemplate.update(
                    "INSERT INTO city (name, country_id, image_url) VALUES ('대구', 1, 'https://example.com/daegu.jpg')");
            jdbcTemplate.update(
                    "INSERT INTO content (title, url, uploaded_date, creator_id, city_id) VALUES ('대구컨텐츠1', 'https://example.com/daegu1', '2024-01-04', 1, 3)");

            // when & then
            RestAssured.given().port(port)
                    .queryParam("isKorea", true)
                    .when().get("/api/v1/region-categories")
                    .then()
                    .statusCode(200)
                    .body("regionCategories.size()", is(3)) // 서울, 부산, 국내 기타 (대구는 국내 기타에 포함)
                    .body("regionCategories[0].regionCategoryName", is("서울")) // 컨텐츠 2개
                    .body("regionCategories[1].regionCategoryName", is("부산")) // 컨텐츠 1개
                    .body("regionCategories[2].regionCategoryName", is("국내 기타")); // 대구 컨텐츠 1개 포함
        }
    }

    @DisplayName("/api/v1/related-spots GET 연관 관광지 조회 테스트")
    @Nested
    class ReadRelatedSpots {

        @DisplayName("한글 지역 카테고리로 연관 관광지 조회 성공 시 200 OK 코드와 연관 관광지 목록을 응답한다")
        @Test
        void readRelatedSpots1() {
            // given
            RelatedSpot spot1 = createRelatedSpot("경복궁", "북촌한옥마을", "관광지");
            RelatedSpot spot2 = createRelatedSpot("경복궁", "인사동", "관광지");
            RelatedSpot spot3 = createRelatedSpot("경복궁", "종로맛집", "음식점");

            given(koreaTourismRelatedSpotClient.searchRelatedSpots(TourApiAreaCode.SEOUL.getAreaCode(),
                    TourApiAreaCode.SEOUL.getSigunguCodes().get(0)))
                    .willReturn(List.of(spot1, spot2, spot3));

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

        @DisplayName("외부 API에서 빈 응답이 오면 200 OK 코드와 빈 목록을 응답한다")
        @Test
        void readRelatedSpots2() {
            // given
            given(koreaTourismRelatedSpotClient.searchRelatedSpots(TourApiAreaCode.BUSAN.getAreaCode(),
                    TourApiAreaCode.BUSAN.getSigunguCodes().get(0)))
                    .willReturn(List.of());

            // when & then
            RestAssured.given().port(port)
                    .queryParam("regionCategory", "부산")
                    .when().get("/api/v1/related-spots")
                    .then()
                    .statusCode(200)
                    .body("relatedSpots.size()", is(0));
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

        private RelatedSpot createRelatedSpot(String touristSpotName, String relatedSpotName,
                                              String relatedCategoryLargeName) {
            RelatedSpot spot = new RelatedSpot();
            try {
                java.lang.reflect.Field touristSpotField = RelatedSpot.class.getDeclaredField("touristSpotName");
                touristSpotField.setAccessible(true);
                touristSpotField.set(spot, touristSpotName);

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
