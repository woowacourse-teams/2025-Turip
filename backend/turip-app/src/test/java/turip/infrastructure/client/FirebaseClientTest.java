package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MessagingErrorCode;
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
    @DisplayName("여러 디바이스에 알림을 성공적으로 전송한다")
    void sendNotificationToMultipleDevicesAndReturnInvalidTokens1() throws FirebaseMessagingException {
        // given
        List<String> fcmTokens = List.of("token1", "token2", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";

        when(firebaseMessaging.send(any(Message.class))).thenReturn("message-id");

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).isEmpty();
        verify(firebaseMessaging, times(3)).send(any(Message.class));
    }

    @Test
    @DisplayName("UNREGISTERED 에러 발생 시 유효하지 않은 토큰 리스트를 반환한다")
    void sendNotificationToMultipleDevicesAndReturnInvalidTokens2() throws FirebaseMessagingException {
        // given
        List<String> fcmTokens = List.of("token1", "invalid-token", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";

        FirebaseMessagingException unregisteredException = mock(FirebaseMessagingException.class);
        when(unregisteredException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.UNREGISTERED);

        when(firebaseMessaging.send(any(Message.class)))
                .thenReturn("message-id")
                .thenThrow(unregisteredException)
                .thenReturn("message-id");

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).hasSize(1).contains("invalid-token");
        verify(firebaseMessaging, times(3)).send(any(Message.class));
    }

    @Test
    @DisplayName("UNREGISTERED가 아닌 다른 에러는 로그만 남기고 계속 진행한다")
    void sendNotificationToMultipleDevicesAndReturnInvalidTokens3() throws FirebaseMessagingException {
        // given
        List<String> fcmTokens = List.of("token1", "token2", "token3");
        String title = "테스트 제목";
        String body = "테스트 내용";

        FirebaseMessagingException otherException = mock(FirebaseMessagingException.class);
        when(otherException.getMessagingErrorCode()).thenReturn(MessagingErrorCode.INTERNAL);

        when(firebaseMessaging.send(any(Message.class)))
                .thenReturn("message-id")
                .thenThrow(otherException)
                .thenReturn("message-id");

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).isEmpty();
        verify(firebaseMessaging, times(3)).send(any(Message.class));
    }
}
