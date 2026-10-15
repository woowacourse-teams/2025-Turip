package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.function.Function;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.GeminiGenerateContentResponse;
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
                [{"placeName":"해운대 해수욕장","confidence":95,"timeSeconds":330,"evidence":"자막 확인"}]""";
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

    private GeminiGenerateContentResponse buildGeminiResponse(String innerJsonText, int totalTokenCount) {
        GeminiGenerateContentResponse.Part part = new GeminiGenerateContentResponse.Part(innerJsonText);
        GeminiGenerateContentResponse.Content content = new GeminiGenerateContentResponse.Content(List.of(part));
        GeminiGenerateContentResponse.Candidate candidate = new GeminiGenerateContentResponse.Candidate(content);
        GeminiGenerateContentResponse.UsageMetadata usageMetadata =
                new GeminiGenerateContentResponse.UsageMetadata(totalTokenCount);
        return new GeminiGenerateContentResponse(List.of(candidate), usageMetadata, "gemini-3.8-flash");
    }
}
