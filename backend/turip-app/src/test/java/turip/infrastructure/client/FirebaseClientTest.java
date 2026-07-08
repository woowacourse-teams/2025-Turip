package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FirebaseClientTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private FirebaseClient firebaseClient;

    @Test
    @DisplayName("FCM 토큰으로 알림을 성공적으로 전송한다")
    void sendNotification1() throws FirebaseMessagingException {
        // given
        String fcmToken = "test-fcm-token";
        String title = "테스트 제목";
        String body = "테스트 내용";

        when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id");

        // when & then
        assertDoesNotThrow(() -> firebaseClient.sendNotification(fcmToken, title, body));
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    @DisplayName("FCM 전송 실패 시 RuntimeException을 발생시킨다")
    void sendNotification2() throws FirebaseMessagingException {
        // given
        String fcmToken = "invalid-token";
        String title = "테스트 제목";
        String body = "테스트 내용";

        when(firebaseMessaging.send(any(Message.class)))
                .thenThrow(FirebaseMessagingException.class);

        // when & then
        assertThatThrownBy(() -> firebaseClient.sendNotification(fcmToken, title, body))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("[FCM] 알림 전송 실패");

        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    @DisplayName("여러 디바이스에 알림을 성공적으로 전송한다")
    void sendNotificationToMultipleDevices() throws FirebaseMessagingException {
        // given
        List<String> fcmTokens = List.of("token1", "token2", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";

        when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id");

        // when & then
        assertDoesNotThrow(() -> firebaseClient.sendNotificationToMultipleDevices(fcmTokens, title, body));
        verify(firebaseMessaging, times(3)).send(any(Message.class));
    }
}
