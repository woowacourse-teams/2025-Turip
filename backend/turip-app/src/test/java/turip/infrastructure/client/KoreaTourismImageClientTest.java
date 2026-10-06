package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.util.Optional;
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

@ActiveProfiles({"test", "h2"})
@RestClientTest(KoreaTourismImageClient.class)
@Import({RestClientConfiguration.class, ExternalApiLoggingInterceptor.class})
class KoreaTourismImageClientTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private KoreaTourismImageClient koreaTourismImageClient;

    @Value("${korea-tourism.api.image-url}")
    private String koreaTourismApiUrl;

    @Value("${korea-tourism.api.key}")
    private String koreaTourismApiKey;

    @Test
    @DisplayName("한국관광공사 이미지 API를 호출하고 응답을 올바르게 파싱한다.")
    void searchRegionImage() {
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
                            "thumbImage": "https://example.com/seoul.jpg"
                          }
                        ]
                      }
                    }
                  }
                }
                """;

        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&MobileOS=AND"
                + "&MobileApp=Turip"
                + "&lDongRegnCd=11"
                + "&_type=json"
                + "&numOfRows=1";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        Optional<String> result = koreaTourismImageClient.searchRegionImage("서울");

        // then
        assertThat(result).contains("https://example.com/seoul.jpg");
        mockRestServiceServer.verify();
    }

    @Test
    @DisplayName("응답의 resultCode가 성공 코드가 아니면 빈 값을 반환한다.")
    void searchRegionImage_withFailureResultCode_returnsEmpty() {
        // given
        String mockResponse = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0001",
                      "resultMsg": "APPLICATION_ERROR"
                    }
                  }
                }
                """;

        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&MobileOS=AND"
                + "&MobileApp=Turip"
                + "&lDongRegnCd=11"
                + "&_type=json"
                + "&numOfRows=1";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        Optional<String> result = koreaTourismImageClient.searchRegionImage("서울");

        // then
        assertThat(result).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    @DisplayName("응답의 items가 비어있을 때 빈 값을 반환한다.")
    void searchRegionImage_withEmptyItems_returnsEmpty() {
        // given
        String mockResponse = """
                {
                  "response": {
                    "header": {
                      "resultCode": "0000",
                      "resultMsg": "OK"
                    },
                    "body": {
                      "items": ""
                    }
                  }
                }
                """;

        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&MobileOS=AND"
                + "&MobileApp=Turip"
                + "&lDongRegnCd=11"
                + "&_type=json"
                + "&numOfRows=1";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        Optional<String> result = koreaTourismImageClient.searchRegionImage("서울");

        // then
        assertThat(result).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    @DisplayName("지원하지 않는 도시명이면 API를 호출하지 않고 빈 값을 반환한다.")
    void searchRegionImage_withUnsupportedCity_returnsEmpty() {
        // when
        Optional<String> result = koreaTourismImageClient.searchRegionImage("존재하지않는도시");

        // then
        assertThat(result).isEmpty();
        mockRestServiceServer.verify();
    }

    @Test
    @DisplayName("API가 5xx 에러를 반환하면 예외를 던지지 않고 빈 값을 반환한다.")
    void searchRegionImage_whenApiReturns5xx_returnsEmpty() {
        // given
        String expectedUrl = koreaTourismApiUrl
                + "?serviceKey=" + koreaTourismApiKey
                + "&MobileOS=AND"
                + "&MobileApp=Turip"
                + "&lDongRegnCd=11"
                + "&_type=json"
                + "&numOfRows=1";

        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withServerError());

        // when
        Optional<String> result = koreaTourismImageClient.searchRegionImage("서울");

        // then
        assertThat(result).isEmpty();
        mockRestServiceServer.verify();
    }
}
