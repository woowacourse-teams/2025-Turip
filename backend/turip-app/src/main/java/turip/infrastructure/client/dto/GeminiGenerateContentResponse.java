package turip.infrastructure.client.dto;

import java.util.List;
import java.util.Optional;

/**
 * Gemini generateContent API의 원시 응답 실제로 사용하는 필드(candidates → content → parts → text, usageMetadata, modelVersion)만 매핑
 *
 * @param candidates     모델이 생성한 응답 후보 목록
 * @param usageMetadata  이 호출의 토큰 사용량
 * @param modelVersion   응답을 생성한 모델명
 * @param promptFeedback 입력이 차단된 경우 그 사유
 */
public record GeminiGenerateContentResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata,
        String modelVersion,
        PromptFeedback promptFeedback
) {

    public Optional<String> extractResponseText() {
        if (candidates == null || candidates.isEmpty()) {
            return Optional.empty();
        }
        Content content = candidates.getFirst().content();
        if (content == null || content.parts() == null || content.parts().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(content.parts().getFirst().text());
    }

    /**
     * 응답 텍스트가 비어 있을 때 원인을 파악하기 위한 정보(생성 종료 사유, 입력 차단 사유)
     */
    public String describeEmptyResponse() {
        String finishReason = candidates == null || candidates.isEmpty() ? null : candidates.getFirst().finishReason();
        String blockReason = promptFeedback == null ? null : promptFeedback.blockReason();
        return "finishReason=" + finishReason + ", blockReason=" + blockReason;
    }

    public Integer totalTokenCount() {
        return usageMetadata == null ? null : usageMetadata.totalTokenCount();
    }

    /**
     * @param finishReason 생성 종료 사유 (STOP, MAX_TOKENS, SAFETY 등)
     */
    public record Candidate(Content content, String finishReason) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    public record PromptFeedback(String blockReason) {
    }

    /**
     * @param promptTokenCount     입력(영상, 프롬프트) 토큰 수
     * @param candidatesTokenCount 출력 토큰 수
     * @param thoughtsTokenCount   추론(thinking) 토큰 수
     * @param totalTokenCount      이 호출에서 사용된 전체 토큰 수
     */
    public record UsageMetadata(
            Integer promptTokenCount,
            Integer candidatesTokenCount,
            Integer thoughtsTokenCount,
            Integer totalTokenCount
    ) {
    }
}
