package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.infrastructure.client.GeminiVideoPlaceInferenceClient;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

@ExtendWith(MockitoExtension.class)
class GeminiVideoPlaceInferenceServiceTest {

    @InjectMocks
    private GeminiVideoPlaceInferenceService fallbackService;

    @Mock
    private GeminiVideoPlaceInferenceClient geminiVideoPlaceInferenceClient;

    private final String youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    @Test
    @DisplayName("1순위 모델이 성공하면 바로 결과를 반환하고 다른 모델은 호출하지 않는다")
    void inferPlaces_whenFirstModelSucceeds_returnsImmediately() {
        // given
        List<GeminiVideoInferredPlace> places = List.of(
                new GeminiVideoInferredPlace("해운대", 95, 330, "근거")
        );
        GeminiVideoPlaceInferenceResponse successResponse =
                GeminiVideoPlaceInferenceResponse.success(places, "gemini-3.8-flash", 1000);
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.8-flash"), eq(youtubeUrl)))
                .thenReturn(successResponse);

        // when
        GeminiVideoPlaceInferenceResponse result = fallbackService.inferPlaces(youtubeUrl);

        // then
        assertThat(result).isEqualTo(successResponse);
        verify(geminiVideoPlaceInferenceClient, never())
                .inferPlaces(eq("gemini-3.7-flash"), anyString());
        verify(geminiVideoPlaceInferenceClient, never())
                .inferPlaces(eq("gemini-3.5-flash-lite"), anyString());
    }

    @Test
    @DisplayName("1순위 모델이 RETRYABLE_FAILURE면 2순위 모델로 전환한다")
    void inferPlaces_whenFirstModelRetryableFailure_fallsBackToSecondModel() {
        // given
        List<GeminiVideoInferredPlace> places = List.of(
                new GeminiVideoInferredPlace("해운대", 95, 330, "근거")
        );
        GeminiVideoPlaceInferenceResponse successResponse =
                GeminiVideoPlaceInferenceResponse.success(places, "gemini-3.7-flash", 1000);
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.8-flash"), eq(youtubeUrl)))
                .thenReturn(GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.8-flash"));
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.7-flash"), eq(youtubeUrl)))
                .thenReturn(successResponse);

        // when
        GeminiVideoPlaceInferenceResponse result = fallbackService.inferPlaces(youtubeUrl);

        // then
        assertThat(result).isEqualTo(successResponse);
    }

    @Test
    @DisplayName("1, 2순위 모델이 모두 RETRYABLE_FAILURE면 3순위 모델로 전환한다")
    void inferPlaces_whenFirstTwoModelsFail_fallsBackToThirdModel() {
        // given
        List<GeminiVideoInferredPlace> places = List.of(
                new GeminiVideoInferredPlace("해운대", 95, 330, "근거")
        );
        GeminiVideoPlaceInferenceResponse successResponse =
                GeminiVideoPlaceInferenceResponse.success(places, "gemini-3.5-flash-lite", 1000);
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.8-flash"), eq(youtubeUrl)))
                .thenReturn(GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.8-flash"));
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.7-flash"), eq(youtubeUrl)))
                .thenReturn(GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.7-flash"));
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.5-flash-lite"), eq(youtubeUrl)))
                .thenReturn(successResponse);

        // when
        GeminiVideoPlaceInferenceResponse result = fallbackService.inferPlaces(youtubeUrl);

        // then
        assertThat(result).isEqualTo(successResponse);
    }

    @Test
    @DisplayName("1순위 모델이 QUOTA_EXCEEDED면 모델 전환 없이 즉시 해당 응답을 반환한다")
    void inferPlaces_whenQuotaExceeded_stopsImmediately() {
        // given
        GeminiVideoPlaceInferenceResponse quotaExceededResponse =
                GeminiVideoPlaceInferenceResponse.quotaExceeded("gemini-3.8-flash");
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.8-flash"), eq(youtubeUrl)))
                .thenReturn(quotaExceededResponse);

        // when
        GeminiVideoPlaceInferenceResponse result = fallbackService.inferPlaces(youtubeUrl);

        // then
        assertThat(result).isEqualTo(quotaExceededResponse);
        verify(geminiVideoPlaceInferenceClient, never())
                .inferPlaces(eq("gemini-3.7-flash"), anyString());
        verify(geminiVideoPlaceInferenceClient, never())
                .inferPlaces(eq("gemini-3.5-flash-lite"), anyString());
    }

    @Test
    @DisplayName("모든 모델이 RETRYABLE_FAILURE면 마지막 모델의 실패 응답을 반환한다")
    void inferPlaces_whenAllModelsFail_returnsLastFailureResponse() {
        // given
        GeminiVideoPlaceInferenceResponse lastFailure =
                GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.5-flash-lite");
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.8-flash"), eq(youtubeUrl)))
                .thenReturn(GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.8-flash"));
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.7-flash"), eq(youtubeUrl)))
                .thenReturn(GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.7-flash"));
        when(geminiVideoPlaceInferenceClient.inferPlaces(eq("gemini-3.5-flash-lite"), eq(youtubeUrl)))
                .thenReturn(lastFailure);

        // when
        GeminiVideoPlaceInferenceResponse result = fallbackService.inferPlaces(youtubeUrl);

        // then
        assertThat(result).isEqualTo(lastFailure);
        assertThat(result.places()).isEmpty();
    }
}
