package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.reactive.function.client.WebClient;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.infrastructure.client.dto.RelatedSpotResult;

class KoreaTourismRelatedSpotClientTest {

    private static final String API_KEY = "test-service-key";

    private MockWebServer mockWebServer;
    private KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        koreaTourismRelatedSpotClient = new KoreaTourismRelatedSpotClient(
                RestClient.builder().build(),
                WebClient.builder().build(),
                API_KEY,
                baseUrl
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("한국관광공사 연관 관광지 API를 호출하고 응답을 올바르게 파싱한다.")
    void searchRelatedSpots() throws InterruptedException {
        // given
        String mockResponse = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": {
                        "item": [
                          {
                            "tAtsCd": "12345",
                            "tAtsNm": "경복궁",
                            "areaCd": "1",
                            "areaNm": "서울특별시",
                            "signguCd": "11110",
                            "signguNm": "종로구",
                            "baseYm": "202606",
                            "rlteTatsCd": "67890",
                            "rlteTatsNm": "북촌한옥마을",
                            "rlteRegnCd": "1",
                            "rlteRegnNm": "서울특별시",
                            "rlteSignguCd": "11110",
                            "rlteSignguNm": "종로구",
                            "rlteCtgryLclsNm": "관광지",
                            "rlteCtgryMclsNm": "역사관광",
                            "rlteCtgrySclsNm": "역사유적지",
                            "rlteRank": 1
                          }
                        ]
                      },
                      "numOfRows": 50,
                      "pageNo": 1,
                      "totalCount": 1
                    }
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        RelatedSpotResult result = koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110).block();

        // then
        assertAll(
                () -> assertThat(result.isSuccess()).isTrue(),
                () -> assertThat(result.spots()).hasSize(1)
        );

        RelatedSpot spot = result.spots().getFirst();
        assertAll(
                () -> assertThat(spot.getTouristSpotName()).isEqualTo("경복궁"),
                () -> assertThat(spot.getAreaName()).isEqualTo("서울특별시"),
                () -> assertThat(spot.getRelatedSpotName()).isEqualTo("북촌한옥마을"),
                () -> assertThat(spot.getRelatedCategoryLargeName()).isEqualTo("관광지"),
                () -> assertThat(spot.getRelatedRank()).isEqualTo(1)
        );

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertThat(recordedRequest.getPath())
                .contains("serviceKey=" + API_KEY)
                .contains("pageNo=1")
                .contains("numOfRows=30")
                .contains("MobileOS=ETC")
                .contains("MobileApp=Turip")
                .contains("_type=json")
                .contains("baseYm=202606")
                .contains("areaCd=11")
                .contains("signguCd=11110");
    }

    @Test
    @DisplayName("응답의 items가 빈 문자열일 때 빈 리스트를 반환한다.")
    void searchRelatedSpots_withEmptyItems_returnsEmptyList() {
        // given
        String mockResponse = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": "",
                      "numOfRows": 30,
                      "pageNo": 1,
                      "totalCount": 0
                    }
                  }
                }
                """;

        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        RelatedSpotResult result = koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110).block();

        // then
        assertAll(
                () -> assertThat(result.isSuccess()).isTrue(),
                () -> assertThat(result.spots()).isEmpty()
        );
    }

    @Test
    @DisplayName("API 호출이 실패하면 실패 결과를 반환한다.")
    void searchRelatedSpots_whenApiFails_returnsFailure() {
        // given
        mockWebServer.enqueue(new MockResponse().setResponseCode(500));

        // when
        RelatedSpotResult result = koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110).block();

        // then
        assertAll(
                () -> assertThat(result.isSuccess()).isFalse(),
                () -> assertThat(result.spots()).isEmpty()
        );
    }

    @Test
    @DisplayName("응답 본문이 완전히 비어있으면 실패 결과를 반환한다.")
    void searchRelatedSpots_whenBodyIsEmpty_returnsFailure() {
        // given
        mockWebServer.enqueue(new MockResponse()
                .setBody("")
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        RelatedSpotResult result = koreaTourismRelatedSpotClient.searchRelatedSpots(11, 11110).block();

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result.isSuccess()).isFalse(),
                () -> assertThat(result.spots()).isEmpty()
        );
    }
}
