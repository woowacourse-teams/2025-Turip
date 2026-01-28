package turip.account.controller.dto.response;

public record MigrationAvailabilityResponse(boolean availability) {

    public static MigrationAvailabilityResponse from(boolean availability) {
        return new MigrationAvailabilityResponse(availability);
    }
}
