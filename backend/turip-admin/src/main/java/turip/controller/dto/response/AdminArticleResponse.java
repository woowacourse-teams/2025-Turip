package turip.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import turip.article.domain.Article;
import turip.place.controller.dto.response.PlaceResponse;

public record AdminArticleResponse(
        Long id,
        String title,
        String subtitle,
        String content,
        String thumbnailUrl,
        boolean isPublished,
        List<String> tags,
        List<PlaceResponse> places,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static AdminArticleResponse of(
            Article article,
            String thumbnailUrl,
            List<String> tagNames,
            List<PlaceResponse> places
    ) {
        return new AdminArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                article.getContent(),
                thumbnailUrl,
                article.isPublished(),
                tagNames,
                places,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
