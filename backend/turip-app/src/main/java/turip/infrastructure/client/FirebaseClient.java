package turip.infrastructure.client;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.Notification;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FirebaseClient {

    private final FirebaseMessaging firebaseMessaging;

    /**
     * 여러 디바이스에 알림을 전송하고 유효하지 않은 토큰을 반환
     *
     * @return 유효하지 않은 토큰 리스트 (UNREGISTERED 에러 발생 시)
     */
    public List<String> sendNotificationToMultipleDevicesAndReturnInvalidTokens(
            Iterable<String> fcmTokens,
            String title,
            String body
    ) {
        List<String> invalidTokens = new ArrayList<>();

        for (String fcmToken : fcmTokens) {
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
                if (e.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                    log.warn("[FCM] 유효하지 않은 토큰: {}", fcmToken);
                    invalidTokens.add(fcmToken);
                } else {
                    log.error("[FCM] 알림 전송 실패: {}", fcmToken, e);
                }
            }
        }

        return invalidTokens;
    }
}
