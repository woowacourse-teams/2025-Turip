package turip.infrastructure.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.GeminiGenerateContentResponse;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

/**
 * Gemini generateContent API에 유튜브 영상 URL을 직접 전달해 영상에 등장하는 방문 장소를 추론하는 클라이언트.
 * 이 클라이언트는 예외를 던지지 않는다 — 성공이든 실패든 항상 {@link GeminiVideoPlaceInferenceResponse}로 결과를 돌려주고,
 * 그 결과를 보고 다음에 무엇을 할지(모델 전환, 즉시 중단 등)는 호출부(서비스 계층)가 결정한다.
 */
@Slf4j
@Component
public class GeminiVideoPlaceInferenceClient {

    private static final double FPS = 0.5;

    private static final String PROMPT = """
            너는 여행 유튜브 영상을 시청하면서 영상에 실제로 등장하는 방문 장소를 식별하는 분석가다.
            첨부된 영상을 처음부터 끝까지 분석하고, 영상에 등장하는 모든 방문 장소(식당, 카페, 관광지, 숙소, 액티비티 장소 등)를 찾아내라.

            판단 기준:
            - 화면에 보이는 간판, 메뉴판, 표지판, 자막, 번인 텍스트를 우선 근거로 삼아라.
            - 텍스트 근거가 없다면 건물 외관, 풍경, 인테리어, 음식 등 시각적 특징으로 추론하고, confidence를 낮게 잡아라.
            - 숙소는 같은 장소가 여러 구간에 등장해도 한 항목으로 합치고, 가장 먼저 등장한 시점을 timeSeconds로 기록하라.
            - 숙소가 아닌 장소(식당, 카페, 관광지, 액티비티 장소 등)는 같은 장소가 여러 구간에 다시 등장하면 각각을 별도 항목으로 남기고, 그때그때 등장한 시점을 timeSeconds로 기록하라.
            - 장소가 아닌 구간(인트로, 이동 중 차 안 등)은 포함하지 마라.

            아래 JSON 배열 스키마로만 응답하라. 다른 설명 텍스트는 포함하지 마라.
            [{"placeName": "장소명", "confidence": 0부터 100 사이 정수, "timeSeconds": 처음 등장한 시점(초), "evidence": "판단 근거 서술"}]
            장소를 하나도 찾을 수 없으면 [] 로 응답하라.
            """;

    private final RestClient restClient;
    private final String geminiApiKey;
    private final ObjectMapper objectMapper;

    public GeminiVideoPlaceInferenceClient(RestClient baseRestClient,
                                           @Value("${gemini.api.key}") String geminiApiKey,
                                           @Value("${gemini.api.base-url}") String geminiBaseUrl,
                                           ObjectMapper objectMapper) {
        this.restClient = baseRestClient.mutate()
                .baseUrl(geminiBaseUrl)
                .build();
        this.geminiApiKey = geminiApiKey;
        this.objectMapper = objectMapper;
    }

    public GeminiVideoPlaceInferenceResponse inferPlaces(String modelName, String youtubeUrl) {
        Map<String, Object> requestBody = buildRequestBody(youtubeUrl);

        GeminiGenerateContentResponse response;
        try {
            response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + modelName + ":generateContent")
                            .queryParam("key", geminiApiKey)
                            .build())
                    .body(requestBody)
                    .retrieve()
                    .onStatus(status -> status.value() == 429, (request, res) -> {
                        throw new QuotaExceededSignal();
                    })
                    .onStatus(HttpStatusCode::isError, (request, res) -> {
                        throw new RetryableFailureSignal();
                    })
                    .body(GeminiGenerateContentResponse.class);
        } catch (QuotaExceededSignal e) {
            log.warn("[Gemini] 영상 장소 추론 쿼터 초과(429): model={}", modelName);
            return GeminiVideoPlaceInferenceResponse.quotaExceeded(modelName);
        } catch (Exception e) {
            log.warn("[Gemini] 영상 장소 추론 호출 실패: model={}, message={}", modelName, e.getMessage());
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName);
        }

        return parseResponse(response, modelName);
    }

    private Map<String, Object> buildRequestBody(String youtubeUrl) {
        Map<String, Object> fileDataPart = Map.of(
                "file_data", Map.of("file_uri", youtubeUrl),
                "video_metadata", Map.of("fps", FPS)
        );
        Map<String, Object> textPart = Map.of("text", PROMPT);

        return Map.of(
                "contents", List.of(Map.of("parts", List.of(fileDataPart, textPart))),
                "generationConfig", Map.of("responseMimeType", "application/json")
        );
    }

    private GeminiVideoPlaceInferenceResponse parseResponse(GeminiGenerateContentResponse response, String modelName) {
        try {
            String text = response.extractResponseText().orElseThrow();
            List<GeminiVideoInferredPlace> places = objectMapper.readValue(
                    text, new TypeReference<List<GeminiVideoInferredPlace>>() {
                    }
            );
            int totalTokenCount = response.usageMetadata().totalTokenCount();
            log.info("[Gemini] 영상 장소 추론 성공: model={}, placeCount={}, totalTokenCount={}",
                    modelName, places.size(), totalTokenCount);
            return GeminiVideoPlaceInferenceResponse.success(places, modelName, totalTokenCount);
        } catch (Exception e) {
            log.warn("[Gemini] 영상 장소 추론 응답 파싱 실패: model={}, message={}", modelName, e.getMessage());
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName);
        }
    }

    private static class QuotaExceededSignal extends RuntimeException {
    }

    private static class RetryableFailureSignal extends RuntimeException {
    }
}
