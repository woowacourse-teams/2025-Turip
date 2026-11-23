package turip.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import turip.infrastructure.client.GeminiVideoPlaceInferenceClient;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiVideoPlaceInferenceService {

    private static final List<String> FALLBACK_MODELS = List.of(
            "gemini-3.8-flash", "gemini-3.7-flash", "gemini-3.5-flash-lite"
    );

    private final GeminiVideoPlaceInferenceClient geminiVideoPlaceInferenceClient;

    public GeminiVideoPlaceInferenceResponse inferPlaces(String youtubeUrl) {
        List<GeminiVideoPlaceInferenceResponse> attempts = new ArrayList<>();

        for (String model : FALLBACK_MODELS) {
            GeminiVideoPlaceInferenceResponse response =
                    geminiVideoPlaceInferenceClient.inferPlaces(model, youtubeUrl);
            attempts.add(response);

            // 성공했거나, 쿼터 초과로 인해 실패한 경우 즉시 종료
            if (response.isSuccess() || response.isQuotaExceeded()) {
                logResult(youtubeUrl, response, attempts);
                return response;
            }

            log.warn("[Gemini] 영상 장소 추론 실패, 다음 모델로 전환: video={}, model={}, reason={}, elapsed={}ms",
                    youtubeUrl, model, response.failureReason(), response.elapsedMs());
        }

        log.error("[Gemini] 영상 장소 추론 모든 모델 실패: video={}, attempts=[{}], totalElapsed={}ms",
                youtubeUrl, summarize(attempts), totalElapsedMs(attempts));
        return attempts.getLast();
    }

    private void logResult(String youtubeUrl, GeminiVideoPlaceInferenceResponse response,
                           List<GeminiVideoPlaceInferenceResponse> attempts) {
        if (response.isQuotaExceeded()) {
            log.warn("[Gemini] 영상 장소 추론 쿼터 초과로 중단: video={}, attempts=[{}], totalElapsed={}ms",
                    youtubeUrl, summarize(attempts), totalElapsedMs(attempts));
            return;
        }
        log.info("[Gemini] 영상 장소 추론 완료: video={}, model={}, placeCount={}, attempts=[{}], totalElapsed={}ms",
                youtubeUrl, response.modelName(), response.places().size(), summarize(attempts),
                totalElapsedMs(attempts));
    }

    private String summarize(List<GeminiVideoPlaceInferenceResponse> attempts) {
        return attempts.stream()
                .map(attempt -> String.format("%s=%s(%.1fs)",
                        attempt.modelName(),
                        attempt.isSuccess() ? "SUCCESS" : attempt.failureReason(),
                        attempt.elapsedMs() / 1000.0))
                .collect(Collectors.joining(", "));
    }

    private long totalElapsedMs(List<GeminiVideoPlaceInferenceResponse> attempts) {
        return attempts.stream()
                .mapToLong(GeminiVideoPlaceInferenceResponse::elapsedMs)
                .sum();
    }
}
