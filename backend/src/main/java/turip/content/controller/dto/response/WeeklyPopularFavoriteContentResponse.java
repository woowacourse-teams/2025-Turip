package turip.content.controller.dto.response;

import turip.content.domain.Content;

public record WeeklyPopularFavoriteContentResponse(
        ContentResponse content,
        TripDurationResponse tripDuration
) {
    public static WeeklyPopularFavoriteContentResponse of(
            Content content,
            boolean isFavorite,
            TripDurationResponse tripDuration
    ) {
        return new WeeklyPopularFavoriteContentResponse(
                ContentResponse.of(content, isFavorite),
                tripDuration
        );
    }
}
