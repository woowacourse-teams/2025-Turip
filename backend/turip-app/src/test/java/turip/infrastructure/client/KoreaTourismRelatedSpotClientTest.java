package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import turip.common.configuration.RestClientConfiguration;
import turip.common.log.ExternalApiLoggingInterceptor;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.region.domain.TourApiAreaCode;

@ActiveProfiles({"test", "h2"})
@RestClientTest(KoreaTourismRelatedSpotClient.class)
@Import({RestClientConfiguration.class, ExternalApiLoggingInterceptor.class})
class KoreaTourismRelatedSpotClientTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private KoreaTourismRelatedSpotClient koreaTourismRelatedSpotClient;

    @Value("${korea-tourism.api.url}")
    private String koreaTourismApiUrl;

    @Value("${korea-tourism.api.key}")
    private String koreaTourismApiKey;

    @Test
    @DisplayName("한국관광공사 연관 관광지 API를 호출하고 응답을 올바르게 파싱한다.")
    void searchRelatedSpots() {
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

        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&pageNo=1"
                + "&numOfRows=50"
                + "&MobileOS=ETC"
                + "&MobileApp=Turip"
                + "&_type=json"
                + "&baseYm=202606"
                + "&areaCd=11"
                + "&signguCd=11110";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        List<RelatedSpot> result = koreaTourismRelatedSpotClient.searchRelatedSpots(TourApiAreaCode.SEOUL);

        // then
        assertAll(
                () -> assertThat(result).isNotNull(),
                () -> assertThat(result).hasSize(1)
        );

        RelatedSpot spot = result.getFirst();
        assertAll(
                () -> assertThat(spot.getTouristSpotName()).isEqualTo("경복궁"),
                () -> assertThat(spot.getAreaName()).isEqualTo("서울특별시"),
                () -> assertThat(spot.getRelatedSpotName()).isEqualTo("북촌한옥마을"),
                () -> assertThat(spot.getRelatedCategoryLargeName()).isEqualTo("관광지"),
                () -> assertThat(spot.getRelatedRank()).isEqualTo(1)
        );

        mockRestServiceServer.verify();
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
                      "numOfRows": 50,
                      "pageNo": 1,
                      "totalCount": 0
                    }
                  }
                }
                """;

        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&pageNo=1"
                + "&numOfRows=50"
                + "&MobileOS=ETC"
                + "&MobileApp=Turip"
                + "&_type=json"
                + "&baseYm=202606"
                + "&areaCd=11"
                + "&signguCd=11110";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        List<RelatedSpot> result = koreaTourismRelatedSpotClient.searchRelatedSpots(TourApiAreaCode.SEOUL);

        // then
        assertThat(result).isEmpty();

        mockRestServiceServer.verify();
    }
}
