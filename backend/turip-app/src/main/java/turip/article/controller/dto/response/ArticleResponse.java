package turip.article.controller.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import turip.article.domain.Article;
import turip.place.controller.dto.response.PlaceResponse;

public record ArticleResponse(
        Long id,
        String title,
        String subtitle,
        String content,
        String thumbnailUrl,
        List<String> tags,
        AuthorResponse author,
        List<PlaceResponse> places,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ArticleResponse of(
            Article article,
            String thumbnailUrl,
            List<String> tagNames,
            List<PlaceResponse> places
    ) {
        return new ArticleResponse(
                article.getId(),
                article.getTitle(),
                article.getSubtitle(),
                article.getContent(),
                thumbnailUrl,
                tagNames,
                AuthorResponse.from(article.getAuthor()),
                places,
                article.getCreatedAt(),
                article.getUpdatedAt()
        );
    }
}
