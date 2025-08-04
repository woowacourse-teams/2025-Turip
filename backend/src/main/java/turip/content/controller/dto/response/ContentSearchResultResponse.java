package turip.content.controller.dto.response;

public record ContentSearchResultResponse(
        ContentWithCreatorAndCityResponse content,
        TripDurationResponse tripDuration,
        int tripPlaceCount
) {

    public static ContentSearchResultResponse of(
            ContentWithCreatorAndCityResponse contentWithCreatorAndCityResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentSearchResultResponse(
                contentWithCreatorAndCityResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
