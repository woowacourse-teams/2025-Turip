package turip.infrastructure.client.dto;

import java.util.List;

/**
 * Gemini 영상 장소 추론 호출 결과
 *
 * @param status 호출 결과 상태
 * @param places 추론된 장소 목록 (실패 시 빈 리스트)
 * @param modelName 이 결과를 만든(또는 시도한) 모델명
 * @param totalTokenCount 이 호출에서 사용된 전체 토큰 수, 실패 시 null
 * @param failureReason 실패 사유 요약(예: "HTTP 503", "TIMEOUT", "EMPTY_RESPONSE"), 성공 시 null
 * @param elapsedMs 이 호출에 걸린 시간(ms)
 */
public record GeminiVideoPlaceInferenceResponse(
        GeminiInferenceStatus status,
        List<GeminiVideoInferredPlace> places,
        String modelName,
        Integer totalTokenCount,
        String failureReason,
        long elapsedMs
) {

    /**
     * Gemini 영상 장소 추론 호출의 결과 상태.
     *
     * @apiNote {@code SUCCESS} 정상 응답을 받아 장소 추론까지 성공
     * @apiNote {@code QUOTA_EXCEEDED} 429 - 프로젝트 쿼터 초과, 다른 모델로 바꿔도 실패하는 경우
     * @apiNote {@code RETRYABLE_FAILURE} 429를 제외한 나머지 모든 실패(5xx, 타임아웃, 응답 파싱 실패 등), 다른 모델로 재시도해볼 가치가 있는 신호
     */
    public enum GeminiInferenceStatus {
        SUCCESS, QUOTA_EXCEEDED, RETRYABLE_FAILURE
    }

    public static GeminiVideoPlaceInferenceResponse success(
            List<GeminiVideoInferredPlace> places, String modelName, Integer totalTokenCount
    ) {
        return new GeminiVideoPlaceInferenceResponse(
                GeminiInferenceStatus.SUCCESS, places, modelName, totalTokenCount, null, 0);
    }

    public static GeminiVideoPlaceInferenceResponse quotaExceeded(String modelName) {
        return new GeminiVideoPlaceInferenceResponse(
                GeminiInferenceStatus.QUOTA_EXCEEDED, List.of(), modelName, null, "HTTP 429", 0);
    }

    public static GeminiVideoPlaceInferenceResponse retryableFailure(String modelName, String failureReason) {
        return new GeminiVideoPlaceInferenceResponse(
                GeminiInferenceStatus.RETRYABLE_FAILURE, List.of(), modelName, null, failureReason, 0);
    }

    public GeminiVideoPlaceInferenceResponse withElapsedMs(long elapsedMs) {
        return new GeminiVideoPlaceInferenceResponse(
                status, places, modelName, totalTokenCount, failureReason, elapsedMs);
    }

    public boolean isSuccess() {
        return status == GeminiInferenceStatus.SUCCESS;
    }

    public boolean isQuotaExceeded() {
        return status == GeminiInferenceStatus.QUOTA_EXCEEDED;
    }
}
