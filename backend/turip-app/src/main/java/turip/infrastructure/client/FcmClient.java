package turip.infrastructure.client;

import java.util.List;

/**
 * FCM 알림 전송을 위한 클라이언트 인터페이스
 */
public interface FcmClient {

    /**
     * 여러 디바이스에 알림을 전송하고 유효하지 않은 토큰을 반환
     *
     * @return 유효하지 않은 토큰 리스트 (UNREGISTERED 에러 발생 시)
     */
    List<String> sendNotificationToMultipleDevicesAndReturnInvalidTokens(
            Iterable<String> fcmTokens,
            String title,
            String body
    );
}
