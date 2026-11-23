package turip.controller.dto.response;

import java.util.List;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

/**
 * AI 장소 추천 결과
 *
 * @param status 추천 결과 상태 (SUCCESS, FAILED, QUOTA_EXCEEDED)
 * @param modelName 결과를 만든(또는 마지막으로 시도한) 모델명
 * @param totalTokenCount 사용한 전체 토큰 수, 실패 시 null
 * @param suggestions 추천 장소 목록 (실패 시 빈 리스트)
 */
public record PlaceSuggestionResponse(
        PlaceSuggestionStatus status,
        String modelName,
        Integer totalTokenCount,
        List<PlaceSuggestionItem> suggestions
) {

    public enum PlaceSuggestionStatus {
        SUCCESS, FAILED, QUOTA_EXCEEDED
    }

    public static PlaceSuggestionResponse from(GeminiVideoPlaceInferenceResponse response) {
        List<PlaceSuggestionItem> items = response.places().stream()
                .map(PlaceSuggestionItem::from)
                .toList();
        return new PlaceSuggestionResponse(
                toStatus(response),
                response.modelName(),
                response.totalTokenCount(),
                items
        );
    }

    private static PlaceSuggestionStatus toStatus(GeminiVideoPlaceInferenceResponse response) {
        if (response.isSuccess()) {
            return PlaceSuggestionStatus.SUCCESS;
        }
        if (response.isQuotaExceeded()) {
            return PlaceSuggestionStatus.QUOTA_EXCEEDED;
        }
        return PlaceSuggestionStatus.FAILED;
    }

    public record PlaceSuggestionItem(String placeName, int confidence, Integer timeSeconds, String evidence) {

        public static PlaceSuggestionItem from(GeminiVideoInferredPlace place) {
            return new PlaceSuggestionItem(place.placeName(), place.confidence(), place.timeSeconds(),
                    place.evidence());
        }
    }
}
