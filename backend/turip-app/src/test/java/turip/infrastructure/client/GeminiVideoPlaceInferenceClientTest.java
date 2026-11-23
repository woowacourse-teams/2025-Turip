package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;
import turip.infrastructure.client.dto.GeminiGenerateContentResponse;
import turip.infrastructure.client.dto.GeminiVideoInferredPlaces;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse.GeminiInferenceStatus;

@ExtendWith(MockitoExtension.class)
class GeminiVideoPlaceInferenceClientTest {

    @Mock
    private RestClient geminiRestClient;

    @Mock
    private RestClient.Builder builder;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestBodyUriSpec requestBodyUriSpec;

    @Mock
    private RestClient.RequestBodySpec requestBodySpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    private GeminiVideoPlaceInferenceClient geminiVideoPlaceInferenceClient;

    @BeforeEach
    void setUp() {
        when(geminiRestClient.mutate()).thenReturn(builder);
        when(builder.baseUrl(anyString())).thenReturn(builder);
        when(builder.build()).thenReturn(restClient);

        geminiVideoPlaceInferenceClient =
                new GeminiVideoPlaceInferenceClient(geminiRestClient, "test-key", "https://test.gemini.com",
                        new ObjectMapper());
    }

    @SuppressWarnings("unchecked")
    private void stubRequestChain() {
        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(Function.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.header(anyString(), any(String[].class))).thenReturn(requestBodySpec);
        when(requestBodySpec.contentType(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
    }

    @Test
    @DisplayName("정상 응답이면 SUCCESS 상태와 장소 추론 리스트, 모델명, 토큰 수를 반환한다")
    void inferPlaces_withValidResponse_returnsSuccessWithPlaces() {
        // given
        stubRequestChain();
        String innerJson = """
                {"places":[{"placeName":"해운대 해수욕장","confidence":95,"timeSeconds":330,"evidence":"자막 확인"}]}""";
        GeminiGenerateContentResponse mockResponse = buildGeminiResponse(innerJson, 1234);
        when(responseSpec.body(GeminiGenerateContentResponse.class)).thenReturn(mockResponse);

        // when
        GeminiVideoPlaceInferenceResponse response =
                geminiVideoPlaceInferenceClient.inferPlaces("gemini-3.8-flash",
                        "https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        // then
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.SUCCESS);
        assertThat(response.modelName()).isEqualTo("gemini-3.8-flash");
        assertThat(response.totalTokenCount()).isEqualTo(1234);
        assertThat(response.places()).hasSize(1);
        assertThat(response.places().get(0).placeName()).isEqualTo("해운대 해수욕장");
    }

    @Test
    @DisplayName("API 키는 URL 쿼리 파라미터가 아니라 x-goog-api-key 헤더로 전달한다")
    @SuppressWarnings("unchecked")
    void inferPlaces_sendsApiKeyViaHeaderNotQueryParam() {
        // given
        stubRequestChain();
        when(responseSpec.body(GeminiGenerateContentResponse.class)).thenReturn(buildGeminiResponse("{\"places\":[]}", 10));

        // when
        geminiVideoPlaceInferenceClient.inferPlaces("gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        // then
        verify(requestBodySpec).header("x-goog-api-key", "test-key");

        ArgumentCaptor<Function<UriBuilder, URI>> uriCaptor = ArgumentCaptor.forClass(Function.class);
        verify(requestBodyUriSpec).uri(uriCaptor.capture());
        URI requestUri = uriCaptor.getValue().apply(UriComponentsBuilder.fromUriString("https://test.gemini.com"));
        assertThat(requestUri.toString())
                .isEqualTo("https://test.gemini.com/gemini-3.8-flash:generateContent")
                .doesNotContain("test-key");
    }

    @Test
    @DisplayName("응답 JSON Schema를 요청의 responseFormat에 담아 Structured outputs로 요청한다")
    @SuppressWarnings("unchecked")
    void inferPlaces_requestsStructuredOutputWithSchema() {
        // given
        stubRequestChain();
        when(responseSpec.body(GeminiGenerateContentResponse.class))
                .thenReturn(buildGeminiResponse("{\"places\":[]}", 10));

        // when
        geminiVideoPlaceInferenceClient.inferPlaces("gemini-3.8-flash", "https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        // then
        ArgumentCaptor<Object> bodyCaptor = ArgumentCaptor.forClass(Object.class);
        verify(requestBodySpec).body(bodyCaptor.capture());
        Map<String, Object> body = (Map<String, Object>) bodyCaptor.getValue();
        Map<String, Object> generationConfig = (Map<String, Object>) body.get("generationConfig");
        Map<String, Object> responseFormat = (Map<String, Object>) generationConfig.get("responseFormat");
        assertThat((Map<String, Object>) responseFormat.get("text"))
                .containsEntry("mimeType", "APPLICATION_JSON")
                .containsEntry("schema", GeminiVideoInferredPlaces.jsonSchema());
    }

    @Test
    @DisplayName("응답의 places가 빈 배열이면 빈 장소 목록으로 성공 처리한다")
    void inferPlaces_withEmptyPlaces_returnsSuccessWithEmptyList() {
        // given
        stubRequestChain();
        when(responseSpec.body(GeminiGenerateContentResponse.class))
                .thenReturn(buildGeminiResponse("{\"places\":[]}", 10));

        // when
        GeminiVideoPlaceInferenceResponse response =
                geminiVideoPlaceInferenceClient.inferPlaces("gemini-3.8-flash",
                        "https://www.youtube.com/watch?v=dQw4w9WgXcQ");

        // then
        assertThat(response.status()).isEqualTo(GeminiInferenceStatus.SUCCESS);
        assertThat(response.places()).isEmpty();
    }

    private GeminiGenerateContentResponse buildGeminiResponse(String innerJsonText, int totalTokenCount) {
        GeminiGenerateContentResponse.Part part = new GeminiGenerateContentResponse.Part(innerJsonText);
        GeminiGenerateContentResponse.Content content = new GeminiGenerateContentResponse.Content(List.of(part));
        GeminiGenerateContentResponse.Candidate candidate = new GeminiGenerateContentResponse.Candidate(content);
        GeminiGenerateContentResponse.UsageMetadata usageMetadata =
                new GeminiGenerateContentResponse.UsageMetadata(totalTokenCount);
        return new GeminiGenerateContentResponse(List.of(candidate), usageMetadata, "gemini-3.8-flash");
    }
}
