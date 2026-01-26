package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@ActiveProfiles("test")
@Import(RestClientConfiguration.class)
@RestClientTest(KakaoPlaceSearchClient.class)
class KakaoPlaceSearchClientTest {

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Autowired
    private KakaoPlaceSearchClient kakaoPlaceSearchClient;

    @Value("${kakao.api.url}")
    private String kakaoApiUrl;

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    @Test
    @DisplayName("카카오 장소 검색 API를 호출하고 응답을 올바르게 파싱한다.")
    void search() {
        // given
        String mockResponse = """
                {
                  "documents": [
                    {
                      "id": "12345",
                      "place_name": "Test Place",
                      "category_name": "Testing > Mock",
                      "address_name": "123, Test Street",
                      "road_address_name": "123, Test Street",
                      "x": "127.12345",
                      "y": "37.54321",
                      "place_url": "http://example.com/place/12345"
                    }
                  ]
                }
                """;
        String query = "testquery";

        mockRestServiceServer.expect(requestTo(kakaoApiUrl + "?query=" + query))
                .andExpect(header("Authorization", "KakaoAK " + kakaoApiKey))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        PlaceSearchResponse response = kakaoPlaceSearchClient.search(query);

        // then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.provider()).isEqualTo(PlaceSearchProvider.KAKAO),
                () -> assertThat(response.items()).hasSize(1)
        );

        PlaceSearchResponse.PlaceSearchItem item = response.items().getFirst();
        assertAll(
                () -> assertThat(item.externalId()).isEqualTo("12345"),
                () -> assertThat(item.name()).isEqualTo("Test Place"),
                () -> assertThat(item.address()).isEqualTo("123, Test Street"),
                () -> assertThat(item.latitude()).isEqualTo(37.54321),
                () -> assertThat(item.longitude()).isEqualTo(127.12345)
        );
        mockRestServiceServer.verify();
    }
}
