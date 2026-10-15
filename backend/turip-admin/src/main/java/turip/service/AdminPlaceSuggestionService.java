package turip.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.BadRequestException;
import turip.controller.dto.response.PlaceSuggestionResponse;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;
import turip.util.YoutubeVideoUrlParser;

@Service
@RequiredArgsConstructor
public class AdminPlaceSuggestionService {

    private final GeminiVideoPlaceInferenceService geminiVideoPlaceInferenceService;

    public PlaceSuggestionResponse getSuggestions(String videoUrl) {
        validateYoutubeUrl(videoUrl);

        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceService.inferPlaces(videoUrl);
        return PlaceSuggestionResponse.from(response);
    }

    private void validateYoutubeUrl(String videoUrl) {
        String videoId = YoutubeVideoUrlParser.extractVideoId(videoUrl);
        if (videoId == null) {
            throw new BadRequestException(ErrorTag.INVALID_YOUTUBE_URL);
        }
    }
}
