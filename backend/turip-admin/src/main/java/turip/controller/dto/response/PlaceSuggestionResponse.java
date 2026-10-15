package turip.controller.dto.response;

import java.util.List;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

public record PlaceSuggestionResponse(
        String modelName,
        Integer totalTokenCount,
        List<PlaceSuggestionItem> suggestions
) {

    public static PlaceSuggestionResponse from(GeminiVideoPlaceInferenceResponse response) {
        List<PlaceSuggestionItem> items = response.places().stream()
                .map(PlaceSuggestionItem::from)
                .toList();
        return new PlaceSuggestionResponse(response.modelName(), response.totalTokenCount(), items);
    }

    public record PlaceSuggestionItem(String placeName, int confidence, Integer timeSeconds, String evidence) {

        public static PlaceSuggestionItem from(GeminiVideoInferredPlace place) {
            return new PlaceSuggestionItem(place.placeName(), place.confidence(), place.timeSeconds(),
                    place.evidence());
        }
    }
}
