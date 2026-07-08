package turip.infrastructure.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FirebaseClient {

    private final FirebaseMessaging firebaseMessaging;

    public void sendNotification(String fcmToken, String title, String body) {
        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("[FCM] 알림 전송 실패", e);
        }
    }

    public void sendNotificationToMultipleDevices(Iterable<String> fcmTokens, String title, String body) {
        for (String fcmToken : fcmTokens) {
            sendNotification(fcmToken, title, body);
        }
    }
}
