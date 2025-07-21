package turip.content.controller.dto.response;

public record TripDurationResponse(
        int nights,
        int days
) {

    // 정책) 당일 치기 여행의 경우 nights 0, days 1로 표현한다.
    public static TripDurationResponse convertToTripDurationFrom(int totalTripDay) {
        boolean dayTrip = totalTripDay == 1;

        if (dayTrip) {
            return new TripDurationResponse(0, totalTripDay);
        }
        return new TripDurationResponse(totalTripDay - 1, totalTripDay);
    }
}
