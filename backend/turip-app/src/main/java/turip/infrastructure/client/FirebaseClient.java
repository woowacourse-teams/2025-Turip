package turip.infrastructure.client;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
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
        List<String> tokenList = new ArrayList<>();
        fcmTokens.forEach(tokenList::add);

        if (tokenList.isEmpty()) {
            return List.of();
        }

        MulticastMessage message = MulticastMessage.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .addAllTokens(tokenList)
                .build();

        List<String> invalidTokens = new ArrayList<>();

        try {
            BatchResponse response = firebaseMessaging.sendEachForMulticast(message);
            List<SendResponse> responses = response.getResponses();

            for (int i = 0; i < responses.size(); i++) {
                SendResponse sendResponse = responses.get(i);
                if (!sendResponse.isSuccessful()) {
                    FirebaseMessagingException exception = sendResponse.getException();
                    String token = tokenList.get(i);

                    if (exception != null && exception.getMessagingErrorCode() == MessagingErrorCode.UNREGISTERED) {
                        log.warn("[FCM] 유효하지 않은 토큰: {}", token);
                        invalidTokens.add(token);
                    } else {
                        log.error("[FCM] 알림 전송 실패: {}", token, exception);
                    }
                }
            }

            log.info("[FCM] 알림 전송 완료 - 성공: {}, 실패: {}", response.getSuccessCount(), response.getFailureCount());
        } catch (FirebaseMessagingException e) {
            log.error("[FCM] MulticastMessage 전송 중 오류 발생", e);
        }

        return invalidTokens;
    }
}
