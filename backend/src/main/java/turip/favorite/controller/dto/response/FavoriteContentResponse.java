package turip.favorite.controller.dto.response;

import java.time.LocalDate;
import turip.content.controller.dto.response.content.ContentResponse;
import turip.favorite.domain.FavoriteContent;

public record FavoriteContentResponse(
        Long id,
        LocalDate createdAt,
        Long memberId,
        ContentResponse content
) {

    public static FavoriteContentResponse from(FavoriteContent favoriteContent) {
        return new FavoriteContentResponse(
                favoriteContent.getId(),
                favoriteContent.getCreatedAt(),
                favoriteContent.getMember().getId(),
                ContentResponse.of(favoriteContent.getContent(), true)
        );
    }
}
