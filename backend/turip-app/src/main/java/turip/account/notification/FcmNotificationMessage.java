package turip.account.notification;

public record FcmNotificationMessage(String title, String message) {

    public static FcmNotificationMessage of(String title, String message) {
        return new FcmNotificationMessage(title, message);
    }
}
