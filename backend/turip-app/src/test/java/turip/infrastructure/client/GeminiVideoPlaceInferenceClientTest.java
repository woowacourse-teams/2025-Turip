package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import turip.common.configuration.RestClientConfiguration;
import turip.common.log.ExternalApiLoggingInterceptor;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse.GeminiInferenceStatus;

@ActiveProfiles({"test", "h2"})
@RestClientTest(GeminiVideoPlaceInferenceClient.class)
@Import({RestClientConfiguration.class, ExternalApiLoggingInterceptor.class})
class GeminiVideoPlaceInferenceClientTest {

    @Autowired
    private GeminiVideoPlaceInferenceClient geminiVideoPlaceInferenceClient;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Value("${gemini.api.base-url}")
    private String geminiBaseUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    @Test
    @DisplayName("정상 응답이면 SUCCESS 상태와 장소 추론 리스트, 모델명, 토큰 수를 반환한다")
    void inferPlaces_withValidResponse_returnsSuccessWithPlaces() {
        // given
        String innerJson = """
                [{"placeName":"해운대 해수욕장","confidence":95,"timeSeconds":330,"evidence":"자막 '해운대 해수욕장' 확인"},{"placeName":"광안리 카페거리","confidence":72,"timeSeconds":null,"evidence":"카페 간판 추정"}]""";
        String mockResponse = buildGeminiResponse(innerJson, 1234);

        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.SUCCESS);
        assertThat(response.modelName()).isEqualTo("gemini-3.8-flash");
        assertThat(response.totalTokenCount()).isEqualTo(1234);
        assertThat(response.places()).containsExactly(
                new GeminiVideoInferredPlace("해운대 해수욕장", 95, 330, "자막 '해운대 해수욕장' 확인"),
                new GeminiVideoInferredPlace("광안리 카페거리", 72, null, "카페 간판 추정")
        );
    }

    @Test
    @DisplayName("confidence가 범위를 벗어나면 0~100으로 clamp한다")
    void inferPlaces_withOutOfRangeConfidence_clampsValue() {
        // given
        String innerJson = """
                [{"placeName":"장소A","confidence":150,"timeSeconds":null,"evidence":"근거A"},{"placeName":"장소B","confidence":-10,"timeSeconds":null,"evidence":"근거B"}]""";
        String mockResponse = buildGeminiResponse(innerJson, 100);

        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        assertThat(response.places()).containsExactly(
                new GeminiVideoInferredPlace("장소A", 100, null, "근거A"),
                new GeminiVideoInferredPlace("장소B", 0, null, "근거B")
        );
    }

    @Test
    @DisplayName("JSON 파싱에 실패하면 RETRYABLE_FAILURE 상태를 반환한다")
    void inferPlaces_withInvalidJson_returnsRetryableFailure() {
        // given
        String mockResponse = buildGeminiResponse("이것은 JSON이 아닙니다", 50);

        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.RETRYABLE_FAILURE);
        assertThat(response.places()).isEmpty();
    }

    @Test
    @DisplayName("Gemini API가 429(쿼터 초과)를 반환하면 QUOTA_EXCEEDED 상태를 반환한다")
    void inferPlaces_whenApiReturns429_returnsQuotaExceeded() {
        // given
        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withStatus(HttpStatus.TOO_MANY_REQUESTS));

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.QUOTA_EXCEEDED);
        assertThat(response.modelName()).isEqualTo("gemini-3.8-flash");
    }

    @Test
    @DisplayName("Gemini API가 429가 아닌 4xx를 반환하면 RETRYABLE_FAILURE 상태를 반환한다")
    void inferPlaces_whenApiReturnsOther4xx_returnsRetryableFailure() {
        // given
        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withBadRequest());

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.RETRYABLE_FAILURE);
    }

    @Test
    @DisplayName("Gemini API가 5xx(과부하 등)를 반환하면 RETRYABLE_FAILURE 상태를 반환한다")
    void inferPlaces_whenApiReturns5xx_returnsRetryableFailure() {
        // given
        String expectedUrl = geminiBaseUrl + "/gemini-3.8-flash:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withServerError());

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.RETRYABLE_FAILURE);
    }

    @Test
    @DisplayName("요청 URL이 모델명에 따라 달라진다")
    void inferPlaces_usesModelNameInUrl() {
        // given
        String mockResponse = buildGeminiResponse("[]", 10);
        String expectedUrl = geminiBaseUrl + "/gemini-3.5-flash-lite:generateContent?key=" + geminiApiKey;
        mockRestServiceServer.expect(requestTo(expectedUrl))
                .andRespond(withSuccess(mockResponse, MediaType.APPLICATION_JSON));

        // when
        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceClient.inferPlaces(
                "gemini-3.5-flash-lite", "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        );

        // then
        mockRestServiceServer.verify();
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.SUCCESS);
        assertThat(response.modelName()).isEqualTo("gemini-3.5-flash-lite");
        assertThat(response.places()).isEmpty();
    }

    private String buildGeminiResponse(String innerJsonText, int totalTokenCount) {
        String escaped = innerJsonText.replace("\"", "\\\"");
        return """
                {
                  "candidates": [
                    {
                      "content": {
                        "parts": [
                          { "text": "%s" }
                        ]
                      }
                    }
                  ],
                  "usageMetadata": { "totalTokenCount": %d },
                  "modelVersion": "gemini-3.8-flash"
                }""".formatted(escaped, totalTokenCount);
    }
}
