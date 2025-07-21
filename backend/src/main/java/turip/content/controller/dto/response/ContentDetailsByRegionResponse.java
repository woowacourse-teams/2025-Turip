package turip.content.controller.dto.response;

public record ContentDetailsByRegionResponse(
        ContentWithoutRegionResponse contentWithoutRegionResponse,
        TripDurationResponse tripDurationResponse,
        int tripPlaceCount
) {

    public static ContentDetailsByRegionResponse of(
            ContentWithoutRegionResponse contentWithoutRegionResponse,
            TripDurationResponse tripDurationResponse,
            int tripPlaceCount
    ) {
        return new ContentDetailsByRegionResponse(
                contentWithoutRegionResponse,
                tripDurationResponse,
                tripPlaceCount
        );
    }
}
