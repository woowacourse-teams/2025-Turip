package turip.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import turip.article.domain.Article;

public record AdminArticleSummaryResponse(
        Long id,
        String title,
        String subtitle,
        String thumbnailUrl,
        boolean isPublished,
        LocalDateTime createdAt,
        List<String> tags
) {

    public static AdminArticleSummaryResponse of(Article article, String thumbnailUrl, List<String> tagNames) {
        return new AdminArticleSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                thumbnailUrl,
                article.isPublished(),
                article.getCreatedAt(),
                tagNames
        );
    }
}
