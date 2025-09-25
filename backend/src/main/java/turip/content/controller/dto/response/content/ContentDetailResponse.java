package turip.content.controller.dto.response.content;

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
} 
