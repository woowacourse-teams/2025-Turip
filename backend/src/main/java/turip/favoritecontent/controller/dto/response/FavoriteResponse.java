package turip.favoritecontent.controller.dto.response;

import java.time.LocalDate;
import turip.content.controller.dto.response.ContentResponse;
import turip.favoritecontent.domain.FavoriteContent;

public record FavoriteResponse(
        Long id,
        LocalDate createdAt,
        Long memberId,
        ContentResponse content
) {

    public static FavoriteResponse from(FavoriteContent favoriteContent) {
        return new FavoriteResponse(
                favoriteContent.getId(),
                favoriteContent.getCreatedAt(),
                favoriteContent.getMember().getId(),
                ContentResponse.of(favoriteContent.getContent(), true)
        );
    }
}
