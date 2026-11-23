package turip.controller.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.controller.dto.response.PlaceSuggestionResponse.PlaceSuggestionStatus;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

class PlaceSuggestionResponseTest {

    @Test
    @DisplayName("추론에 성공하면 SUCCESS 상태와 추천 목록을 담는다")
    void from_whenSuccess_returnsSuccessStatus() {
        // given
        GeminiVideoPlaceInferenceResponse inference = GeminiVideoPlaceInferenceResponse.success(
                List.of(new GeminiVideoInferredPlace("해운대", 95, 330, "자막 확인")), "gemini-3.8-flash", 1234);

        // when
        PlaceSuggestionResponse response = PlaceSuggestionResponse.from(inference);

        // then
        assertThat(response.status()).isEqualTo(PlaceSuggestionStatus.SUCCESS);
        assertThat(response.suggestions()).hasSize(1);
    }

    @Test
    @DisplayName("쿼터를 초과하면 QUOTA_EXCEEDED 상태를 담는다")
    void from_whenQuotaExceeded_returnsQuotaExceededStatus() {
        // when
        PlaceSuggestionResponse response = PlaceSuggestionResponse.from(
                GeminiVideoPlaceInferenceResponse.quotaExceeded("gemini-3.8-flash"));

        // then
        assertThat(response.status()).isEqualTo(PlaceSuggestionStatus.QUOTA_EXCEEDED);
    }

    @Test
    @DisplayName("쿼터 초과를 제외한 실패는 FAILED 상태와 빈 추천 목록을 담는다")
    void from_whenFailed_returnsFailedStatus() {
        // when
        PlaceSuggestionResponse response = PlaceSuggestionResponse.from(
                GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.5-flash-lite", "HTTP 503"));

        // then
        assertThat(response.status()).isEqualTo(PlaceSuggestionStatus.FAILED);
        assertThat(response.suggestions()).isEmpty();
    }
}
