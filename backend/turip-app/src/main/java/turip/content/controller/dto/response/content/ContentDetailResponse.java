package turip.content.controller.dto.response.content;

import turip.favorite.controller.dto.response.FavoriteContentDetailResponse;

public record ContentDetailResponse(
        ContentResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentDetailResponse of(
            ContentResponse content,
            TripDurationResponse tripDuration,
            int tripPlaceCount
    ) {
        return new ContentDetailResponse(
                content,
                tripDuration,
                tripPlaceCount
        );
    }

    public static ContentDetailResponse from(FavoriteContentDetailResponse favoriteContentDetailResponse) {
        return new ContentDetailResponse(
                favoriteContentDetailResponse.content(),
                favoriteContentDetailResponse.tripDuration(),
                favoriteContentDetailResponse.tripPlaceCount()
        );
    }
}
