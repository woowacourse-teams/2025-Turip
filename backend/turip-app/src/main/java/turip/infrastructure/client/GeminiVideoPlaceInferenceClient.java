package turip.infrastructure.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import turip.infrastructure.client.dto.GeminiGenerateContentResponse;
import turip.infrastructure.client.dto.GeminiGenerateContentResponse.UsageMetadata;
import turip.infrastructure.client.dto.GeminiVideoInferredPlace;
import turip.infrastructure.client.dto.GeminiVideoInferredPlaces;
import turip.infrastructure.client.dto.GeminiVideoPlaceInferenceResponse;

/**
 * Gemini generateContent API에 유튜브 영상 URL을 직접 전달해 영상에 등장하는 방문 장소를 추론하는 클라이언트.
 * 이 클라이언트는 예외를 던지지 않는다 — 성공이든 실패든 항상 {@link GeminiVideoPlaceInferenceResponse}로 결과를 돌려주고,
 * 그 결과를 보고 다음에 무엇을 할지(모델 전환, 즉시 중단 등)는 호출부(서비스 계층)가 결정한다.
 */
@Slf4j
@Component
public class GeminiVideoPlaceInferenceClient {

    private static final String API_KEY_HEADER = "x-goog-api-key";
    private static final int LOG_TEXT_MAX_LENGTH = 500;

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
            """;

    private final RestClient restClient;
    private final String geminiApiKey;
    private final ObjectMapper objectMapper;

    public GeminiVideoPlaceInferenceClient(@Qualifier("geminiRestClient") RestClient geminiRestClient,
                                           @Value("${gemini.api.key}") String geminiApiKey,
                                           @Value("${gemini.api.base-url}") String geminiBaseUrl,
                                           ObjectMapper objectMapper) {
        this.restClient = geminiRestClient.mutate()
                .baseUrl(geminiBaseUrl)
                .build();
        this.geminiApiKey = geminiApiKey;
        this.objectMapper = objectMapper;
    }

    public GeminiVideoPlaceInferenceResponse inferPlaces(String modelName, String youtubeUrl) {
        long startedAt = System.currentTimeMillis();
        GeminiVideoPlaceInferenceResponse result = request(modelName, youtubeUrl);
        return result.withElapsedMs(System.currentTimeMillis() - startedAt);
    }

    private GeminiVideoPlaceInferenceResponse request(String modelName, String youtubeUrl) {
        Map<String, Object> requestBody = buildRequestBody(youtubeUrl);

        GeminiGenerateContentResponse response;
        try {
            response = restClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/" + modelName + ":generateContent")
                            .build())
                    .header(API_KEY_HEADER, geminiApiKey)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .onStatus(status -> status.value() == 429, (request, res) -> {
                        throw new QuotaExceededSignal();
                    })
                    .onStatus(HttpStatusCode::isError, (request, res) -> {
                        throw new HttpErrorSignal(res.getStatusCode().value(), readBody(res));
                    })
                    .body(GeminiGenerateContentResponse.class);
        } catch (QuotaExceededSignal e) {
            log.warn("[Gemini] 영상 장소 추론 쿼터 초과(429): model={}, video={}", modelName, youtubeUrl);
            return GeminiVideoPlaceInferenceResponse.quotaExceeded(modelName);
        } catch (HttpErrorSignal e) {
            log.warn("[Gemini] 영상 장소 추론 HTTP 오류: model={}, video={}, status={}, body={}",
                    modelName, youtubeUrl, e.statusCode, e.body);
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName, "HTTP " + e.statusCode);
        } catch (Exception e) {
            if (isTimeout(e)) {
                log.warn("[Gemini] 영상 장소 추론 타임아웃: model={}, video={}", modelName, youtubeUrl);
                return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName, "TIMEOUT");
            }
            log.warn("[Gemini] 영상 장소 추론 호출 실패: model={}, video={}, exception={}, message={}",
                    modelName, youtubeUrl, e.getClass().getSimpleName(), e.getMessage());
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName,
                    "REQUEST_ERROR(" + e.getClass().getSimpleName() + ")");
        }

        return parseResponse(response, modelName, youtubeUrl);
    }

    private Map<String, Object> buildRequestBody(String youtubeUrl) {
        Map<String, Object> fileDataPart = Map.of(
                "file_data", Map.of("file_uri", youtubeUrl),
                "video_metadata", Map.of("fps", FPS)
        );
        Map<String, Object> textPart = Map.of("text", PROMPT);

        return Map.of(
                "contents", List.of(Map.of("parts", List.of(fileDataPart, textPart))),
                "generationConfig", Map.of(
                        "responseFormat", Map.of("text", Map.of(
                                "mimeType", "APPLICATION_JSON",
                                "schema", GeminiVideoInferredPlaces.jsonSchema()
                        ))
                )
        );
    }

    private GeminiVideoPlaceInferenceResponse parseResponse(GeminiGenerateContentResponse response, String modelName,
                                                            String youtubeUrl) {
        if (response == null || response.extractResponseText().isEmpty()) {
            String emptyReason = response == null ? "응답 본문 없음" : response.describeEmptyResponse();
            log.warn("[Gemini] 영상 장소 추론 응답이 비어 있음: model={}, video={}, {}", modelName, youtubeUrl, emptyReason);
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName, "EMPTY_RESPONSE(" + emptyReason + ")");
        }

        String text = response.extractResponseText().get();
        List<GeminiVideoInferredPlace> places;
        try {
            places = objectMapper.readValue(text, GeminiVideoInferredPlaces.class).places();
        } catch (Exception e) {
            log.warn("[Gemini] 영상 장소 추론 응답 파싱 실패: model={}, video={}, message={}, text={}",
                    modelName, youtubeUrl, e.getMessage(), abbreviate(text));
            return GeminiVideoPlaceInferenceResponse.retryableFailure(modelName, "PARSE_FAILURE");
        }

        UsageMetadata usage = response.usageMetadata();
        log.info("[Gemini] 영상 장소 추론 성공: model={}, modelVersion={}, video={}, placeCount={}, "
                        + "promptTokens={}, outputTokens={}, thoughtsTokens={}, totalTokens={}",
                modelName, response.modelVersion(), youtubeUrl, places.size(),
                usage == null ? null : usage.promptTokenCount(),
                usage == null ? null : usage.candidatesTokenCount(),
                usage == null ? null : usage.thoughtsTokenCount(),
                response.totalTokenCount());
        return GeminiVideoPlaceInferenceResponse.success(places, modelName, response.totalTokenCount());
    }

    private static boolean isTimeout(Throwable e) {
        for (Throwable cause = e; cause != null; cause = cause.getCause()) {
            if (cause instanceof HttpTimeoutException || cause instanceof SocketTimeoutException) {
                return true;
            }
        }
        return false;
    }

    private static String readBody(ClientHttpResponse response) {
        try {
            return abbreviate(new String(response.getBody().readAllBytes(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            return "(응답 본문을 읽지 못함)";
        }
    }

    private static String abbreviate(String text) {
        String oneLine = text.replaceAll("\\s+", " ");
        return oneLine.length() > LOG_TEXT_MAX_LENGTH ? oneLine.substring(0, LOG_TEXT_MAX_LENGTH) + "..." : oneLine;
    }

    private static class QuotaExceededSignal extends RuntimeException {
    }

    private static class HttpErrorSignal extends RuntimeException {

        private final int statusCode;
        private final String body;

        HttpErrorSignal(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }
}
