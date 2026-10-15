package turip.infrastructure.client.dto;

/**
 * Gemini가 유튜브 영상을 직접 분석해 추론한 방문 장소 하나
 *
 * @param placeName 장소명
 * @param confidence 판단한 장소의 정확도 (0~100)
 * @param timeSeconds 장소 출현 시간(타임라인), 알 수 없으면 null
 * @param evidence 장소 판단 근거
 */
public record GeminiVideoInferredPlace(
        String placeName,
        int confidence,
        Integer timeSeconds,
        String evidence
) {

    public GeminiVideoInferredPlace {
        confidence = Math.max(0, Math.min(100, confidence));
    }
}
