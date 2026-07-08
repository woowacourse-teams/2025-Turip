package turip.account.notification;

public record FcmAlertMessage(String title, String message) {

    public static FcmAlertMessage of(String title, String message) {
        return new FcmAlertMessage(title, message);
    }
}
