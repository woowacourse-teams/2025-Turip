package turip.place.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@RestClientTest(GooglePlaceSearchClient.class)
class GooglePlaceSearchClientTest {

    private static MockWebServer mockWebServer;
    @Autowired
    private GooglePlaceSearchClient googlePlaceSearchClient;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void dynamicPropertySource(DynamicPropertyRegistry registry) {
        registry.add("google.api.url", () -> mockWebServer.url("/").toString());
        registry.add("google.api.key", () -> "test-google-api-key");
    }

    @Test
    @DisplayName("구글 장소 검색 API를 호출하고 응답을 올바르게 파싱한다.")
    void search() throws InterruptedException {
        // given
        String query = "testquery";
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
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE));

        // when
        PlaceSearchResponse response = googlePlaceSearchClient.search(query);

        // then
        assertAll("Response DTO validation",
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.provider()).isEqualTo(PlaceSearchProvider.GOOGLE),
                () -> assertThat(response.items()).hasSize(1)
        );

        PlaceSearchResponse.PlaceSearchItem item = response.items().get(0);
        assertAll("PlaceSearchItem validation",
                () -> assertThat(item.externalId()).isEqualTo("google123"),
                () -> assertThat(item.name()).isEqualTo("Google Test Place"),
                () -> assertThat(item.address()).isEqualTo("123, Google Test Street"),
                () -> assertThat(item.latitude()).isEqualTo(34.0522),
                () -> assertThat(item.longitude()).isEqualTo(-118.2437),
                () -> assertThat(item.categoryName()).isEqualTo("tourist_attraction")
        );

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertAll("HTTP Request validation",
                () -> assertThat(recordedRequest.getMethod()).isEqualTo("GET"),
                () -> {
                    String expectedPath = "/?query=" + query + "&key=test-google-api-key";
                    assertThat(recordedRequest.getPath()).isEqualTo(expectedPath);
                }
        );
    }
}
