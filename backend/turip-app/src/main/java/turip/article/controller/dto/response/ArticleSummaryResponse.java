package turip.article.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import turip.article.domain.Article;

public record ArticleSummaryResponse(
        Long id,
        String title,
        String subtitle,
        String thumbnailUrl,
        LocalDateTime createdAt,
        List<String> tags,
        AuthorResponse author
) {

    public static ArticleSummaryResponse of(Article article, String thumbnailUrl, List<String> tagNames) {
        return new ArticleSummaryResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                thumbnailUrl,
                article.getCreatedAt(),
                tagNames,
                AuthorResponse.from(article.getAuthor())
        );
    }
}
