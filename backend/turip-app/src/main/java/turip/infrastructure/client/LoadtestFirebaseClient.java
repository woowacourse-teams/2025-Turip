package turip.infrastructure.client;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * 로드테스트용 Firebase 클라이언트
 * 실제 FCM 서버를 호출하지 않고 네트워크 지연만 시뮬레이션합니다.
 */
@Slf4j
@Component
@Profile("loadtest")
public class LoadtestFirebaseClient implements FcmClient {

    /**
     * 여러 디바이스에 알림을 전송하고 유효하지 않은 토큰을 반환
     * loadtest 환경에서는 실제 FCM 호출 없이 네트워크 지연만 시뮬레이션합니다.
     *
     * @return 유효하지 않은 토큰 리스트 (항상 빈 리스트 반환)
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

        try {
            // 실제 FCM 호출을 시뮬레이션하기 위한 네트워크 지연 (150ms)
            Thread.sleep(150);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("[LOADTEST-FCM] 지연 시뮬레이션 중 인터럽트 발생", e);
        }

        log.info("[LOADTEST-FCM] 알림 전송 시뮬레이션 완료 - 성공: {}, 실패: 0, 제목: {}", tokenList.size(), title);

        // loadtest 환경에서는 모든 토큰이 유효하다고 가정 (빈 리스트 반환)
        return List.of();
    }
}
