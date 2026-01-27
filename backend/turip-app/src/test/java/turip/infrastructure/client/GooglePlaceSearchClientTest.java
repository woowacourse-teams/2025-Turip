package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import turip.common.log.ExternalApiLoggingInterceptor;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@ActiveProfiles("test")
@RestClientTest(GooglePlaceSearchClient.class)
@Import({RestClientConfiguration.class, ExternalApiLoggingInterceptor.class})
class GooglePlaceSearchClientTest {

    @Autowired
    private GooglePlaceSearchClient googlePlaceSearchClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Value("${google.api.url}")
    private String googleApiUrl;

    @Value("${google.api.key}")
    private String googleApiKey;

    @Test
    @DisplayName("구글 장소 검색 API를 호출하고 응답을 올바르게 파싱한다.")
    void search() {
        // given
        String mockResponse = """
                {
                  "results": [
                    {
                      "place_id": "google123",
                      "name": "Google Test Place",
                      "formatted_address": "123, Google Test Street",
                      "website": "https://google.example.com",
                      "types": ["tourist_attraction"],
                      "geometry": {
                        "location": {
                          "lat": 34.0522,
                          "lng": -118.2437
                        }
                      }
                    }
                  ]
                }
                """;
        String query = "testquery";

        this.mockRestServiceServer.expect(requestTo(googleApiUrl + "?query=" + query + "&key=" + googleApiKey))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        PlaceSearchResponse response = googlePlaceSearchClient.search(query);

        // then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.provider()).isEqualTo(PlaceSearchProvider.GOOGLE),
                () -> assertThat(response.items()).hasSize(1)
        );

        PlaceSearchResponse.PlaceSearchItem item = response.items().getFirst();
        assertAll(
                () -> assertThat(item.externalId()).isEqualTo("google123"),
                () -> assertThat(item.name()).isEqualTo("Google Test Place"),
                () -> assertThat(item.address()).isEqualTo("123, Google Test Street"),
                () -> assertThat(item.latitude()).isEqualTo(34.0522),
                () -> assertThat(item.longitude()).isEqualTo(-118.2437),
                () -> assertThat(item.categoryName()).isEqualTo("tourist_attraction")
        );
        this.mockRestServiceServer.verify();
    }
}
