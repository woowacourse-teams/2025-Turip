package turip.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.controller.dto.response.PlaceSuggestionResponse;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

@ExtendWith(MockitoExtension.class)
class AdminPlaceSuggestionServiceTest {

    @InjectMocks
    private AdminPlaceSuggestionService adminPlaceSuggestionService;

    @Mock
    private GeminiVideoPlaceInferenceService geminiVideoPlaceInferenceService;

    private final String videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    @Test
    @DisplayName("Gemini가 정상 응답하면 모델명, 토큰 수, 추천 리스트를 그대로 반환한다")
    void getSuggestions_whenGeminiSucceeds_returnsSuggestions() {
        // given
        List<GeminiVideoInferredPlace> places = List.of(
                new GeminiVideoInferredPlace("해운대 해수욕장", 95, 330, "자막 확인")
        );
        GeminiVideoPlaceInferenceResponse geminiResponse =
                GeminiVideoPlaceInferenceResponse.success(places, "gemini-3.8-flash", 1234);
        when(geminiVideoPlaceInferenceService.inferPlaces(videoUrl)).thenReturn(geminiResponse);

        // when
        PlaceSuggestionResponse response = adminPlaceSuggestionService.getSuggestions(videoUrl);

        // then
        assertThat(response.modelName()).isEqualTo("gemini-3.8-flash");
        assertThat(response.totalTokenCount()).isEqualTo(1234);
        assertThat(response.suggestions()).hasSize(1);
        assertThat(response.suggestions().get(0).placeName()).isEqualTo("해운대 해수욕장");
    }

    @Test
    @DisplayName("모든 모델이 실패하면 빈 추천 리스트와 null 토큰 수를 반환한다")
    void getSuggestions_whenGeminiFails_returnsEmptySuggestionsWithNullTokenCount() {
        // given
        GeminiVideoPlaceInferenceResponse geminiResponse =
                GeminiVideoPlaceInferenceResponse.retryableFailure("gemini-3.5-flash-lite");
        when(geminiVideoPlaceInferenceService.inferPlaces(videoUrl)).thenReturn(geminiResponse);

        // when
        PlaceSuggestionResponse response = adminPlaceSuggestionService.getSuggestions(videoUrl);

        // then
        assertThat(response.suggestions()).isEmpty();
        assertThat(response.totalTokenCount()).isNull();
    }

    @Test
    @DisplayName("유효하지 않은 유튜브 URL이면 BadRequestException을 던진다")
    void getSuggestions_withInvalidUrl_throwsBadRequestException() {
        // given
        String invalidUrl = "https://invalid.url/test";

        // when & then
        assertThatThrownBy(() -> adminPlaceSuggestionService.getSuggestions(invalidUrl))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(ErrorTag.INVALID_YOUTUBE_URL.getMessage());
    }
}
