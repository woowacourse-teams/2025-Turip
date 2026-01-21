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
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchProvider;

@RestClientTest(KakaoPlaceSearchClient.class)
class KakaoPlaceSearchClientTest {

    private static MockWebServer mockWebServer;
    @Autowired
    private KakaoPlaceSearchClient kakaoPlaceSearchClient;

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
        registry.add("kakao.api.url", () -> mockWebServer.url("/").toString());
        registry.add("kakao.api.key", () -> "test-api-key");
    }

    @Test
    @DisplayName("카카오 장소 검색 API를 호출하고 응답을 올바르게 파싱한다.")
    void search() throws InterruptedException {
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
        mockWebServer.enqueue(new MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json"));

        String query = "testquery";

        // when
        PlaceSearchResponse response = kakaoPlaceSearchClient.search(query);

        // then
        assertAll(
                () -> assertThat(response).isNotNull(),
                () -> assertThat(response.provider()).isEqualTo(PlaceSearchProvider.KAKAO),
                () -> assertThat(response.items()).hasSize(1)
        );

        PlaceSearchResponse.PlaceSearchItem item = response.items().get(0);
        assertAll(
                () -> assertThat(item.externalId()).isEqualTo("12345"),
                () -> assertThat(item.name()).isEqualTo("Test Place"),
                () -> assertThat(item.address()).isEqualTo("123, Test Street"),
                () -> assertThat(item.latitude()).isEqualTo(37.54321),
                () -> assertThat(item.longitude()).isEqualTo(127.12345)
        );

        RecordedRequest recordedRequest = mockWebServer.takeRequest();
        assertAll(
                () -> assertThat(recordedRequest.getMethod()).isEqualTo("GET"),
                () -> assertThat(recordedRequest.getPath()).isEqualTo("/?query=" + query),
                () -> assertThat(recordedRequest.getHeader("Authorization")).isEqualTo("KakaoAK test-api-key")
        );
    }
}
