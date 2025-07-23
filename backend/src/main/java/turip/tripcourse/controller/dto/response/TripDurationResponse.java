package turip.tripcourse.controller.dto.response;

public record TripDurationResponse(
        int nights,
        int days
) {
    public static TripDurationResponse of(int nights, int days) {
        return new TripDurationResponse(nights, days);
    }
}
