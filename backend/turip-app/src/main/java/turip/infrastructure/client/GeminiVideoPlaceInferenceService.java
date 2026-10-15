package turip.infrastructure.client;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
        GeminiVideoPlaceInferenceResponse lastResponse = null;

        for (String model : FALLBACK_MODELS) {
            GeminiVideoPlaceInferenceResponse response =
                    geminiVideoPlaceInferenceClient.inferPlaces(model, youtubeUrl);

            // 성공했거나, 쿼터 초과로 인해 실패한 경우 즉시 종료
            if (response.isSuccess() || response.isQuotaExceeded()) {
                return response;
            }

            log.warn("[Gemini] 영상 장소 추론 model={} 실패 — 다음 모델로 전환", model);
            lastResponse = response;
        }

        log.warn("[Gemini] 영상 장소 추론 모든 모델 폴백 실패");
        return lastResponse;
    }
}
