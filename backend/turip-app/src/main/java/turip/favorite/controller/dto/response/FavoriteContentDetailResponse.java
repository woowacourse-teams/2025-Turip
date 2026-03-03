package turip.favorite.controller.dto.response;

import java.time.LocalDate;
import turip.content.controller.dto.response.content.ContentResponse;
import turip.content.controller.dto.response.content.TripDurationResponse;
import turip.favorite.domain.FavoriteContent;

public record FavoriteContentDetailResponse(
        Long id,
        LocalDate createdAt,
        Long accountId,
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static FavoriteContentDetailResponse of(
            FavoriteContent favoriteContent,
            TripDurationResponse tripDuration,
            int tripPlaceCount
    ) {
        return new FavoriteContentDetailResponse(
                favoriteContent.getId(),
                favoriteContent.getCreatedAt(),
                favoriteContent.getAccount().getId(),
                ContentResponse.of(favoriteContent.getContent(), true),
                tripDuration,
                tripPlaceCount
        );
    }
}
