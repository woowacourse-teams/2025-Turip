package turip.favorite.controller.dto.response;

import java.time.LocalDate;
import turip.content.controller.dto.response.ContentResponse;
import turip.favorite.domain.Favorite;

public record FavoriteResponse(
        Long id,
        LocalDate createdAt,
        Long memberId,
        ContentResponse content
) {

    public static FavoriteResponse from(Favorite favorite) {
        return new FavoriteResponse(
                favorite.getId(),
                favorite.getCreatedAt(),
                favorite.getMember().getId(),
                ContentResponse.from(favorite.getContent())
        );
    }
}
