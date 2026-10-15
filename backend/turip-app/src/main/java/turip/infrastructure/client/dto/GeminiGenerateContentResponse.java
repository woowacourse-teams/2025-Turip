package turip.infrastructure.client.dto;

import java.util.List;
import java.util.Optional;

/**
 * Gemini generateContent API의 원시 응답 실제로 사용하는 필드(candidates → content → parts → text, usageMetadata, modelVersion)만 매핑
 *
 * @param candidates    모델이 생성한 응답 후보 목록
 * @param usageMetadata 이 호출의 토큰 사용량
 * @param modelVersion  응답을 생성한 모델명
 */
public record GeminiGenerateContentResponse(
        List<Candidate> candidates,
        UsageMetadata usageMetadata,
        String modelVersion
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

    public record Candidate(Content content) {
    }

    public record Content(List<Part> parts) {
    }

    public record Part(String text) {
    }

    /**
     * @param totalTokenCount 이 호출에서 사용된 전체 토큰 수(입력+출력)
     */
    public record UsageMetadata(int totalTokenCount) {
    }
}
