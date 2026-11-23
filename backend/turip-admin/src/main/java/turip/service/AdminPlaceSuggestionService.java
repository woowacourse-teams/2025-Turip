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
        String normalizedVideoUrl = normalizeYoutubeUrl(videoUrl);

        GeminiVideoPlaceInferenceResponse response = geminiVideoPlaceInferenceService.inferPlaces(normalizedVideoUrl);
        return PlaceSuggestionResponse.from(response);
    }

    /**
     * 입력값에서 videoId를 추출해, 부가 파라미터가 없는 정규 watch URL로 바꾼다.
     * videoId만 입력하거나 재생 시작 시점(t) 등이 붙은 URL도 Gemini가 영상 전체를 분석할 수 있는 형태로 맞춘다.
     */
    private String normalizeYoutubeUrl(String videoUrl) {
        String videoId = YoutubeVideoUrlParser.extractVideoId(videoUrl);
        if (videoId == null) {
            throw new BadRequestException(ErrorTag.INVALID_YOUTUBE_URL);
        }
        return YoutubeVideoUrlParser.toWatchUrl(videoId);
    }
}
