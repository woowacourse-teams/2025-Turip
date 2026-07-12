package turip.infrastructure.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
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

        SendResponse successResponse1 = mock(SendResponse.class);
        SendResponse successResponse2 = mock(SendResponse.class);
        SendResponse successResponse3 = mock(SendResponse.class);
        when(successResponse1.isSuccessful()).thenReturn(true);
        when(successResponse2.isSuccessful()).thenReturn(true);
        when(successResponse3.isSuccessful()).thenReturn(true);

        BatchResponse batchResponse = mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(List.of(successResponse1, successResponse2, successResponse3));
        when(batchResponse.getSuccessCount()).thenReturn(3);
        when(batchResponse.getFailureCount()).thenReturn(0);

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batchResponse);

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).isEmpty();
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
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

        SendResponse successResponse1 = mock(SendResponse.class);
        when(successResponse1.isSuccessful()).thenReturn(true);

        SendResponse failedResponse = mock(SendResponse.class);
        when(failedResponse.isSuccessful()).thenReturn(false);
        when(failedResponse.getException()).thenReturn(unregisteredException);

        SendResponse successResponse3 = mock(SendResponse.class);
        when(successResponse3.isSuccessful()).thenReturn(true);

        BatchResponse batchResponse = mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(List.of(successResponse1, failedResponse, successResponse3));
        when(batchResponse.getSuccessCount()).thenReturn(2);
        when(batchResponse.getFailureCount()).thenReturn(1);

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batchResponse);

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).hasSize(1).contains("invalid-token");
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
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

        SendResponse successResponse1 = mock(SendResponse.class);
        when(successResponse1.isSuccessful()).thenReturn(true);

        SendResponse failedResponse = mock(SendResponse.class);
        when(failedResponse.isSuccessful()).thenReturn(false);
        when(failedResponse.getException()).thenReturn(otherException);

        SendResponse successResponse3 = mock(SendResponse.class);
        when(successResponse3.isSuccessful()).thenReturn(true);

        BatchResponse batchResponse = mock(BatchResponse.class);
        when(batchResponse.getResponses()).thenReturn(List.of(successResponse1, failedResponse, successResponse3));
        when(batchResponse.getSuccessCount()).thenReturn(2);
        when(batchResponse.getFailureCount()).thenReturn(1);

        when(firebaseMessaging.sendEachForMulticast(any(MulticastMessage.class))).thenReturn(batchResponse);

        // when
        List<String> invalidTokens = firebaseClient.sendNotificationToMultipleDevicesAndReturnInvalidTokens(
                fcmTokens, title, body
        );

        // then
        assertThat(invalidTokens).isEmpty();
        verify(firebaseMessaging, times(1)).sendEachForMulticast(any(MulticastMessage.class));
    }
}
