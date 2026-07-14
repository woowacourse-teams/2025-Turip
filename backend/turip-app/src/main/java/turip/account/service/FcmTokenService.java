package turip.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.account.domain.Account;
import turip.account.domain.FcmToken;
import turip.account.repository.FcmTokenRepository;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.NotFoundException;

@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void registerOrUpdate(Account account, String deviceFid, String token) {
        reassignTokenIfOwnedByOther(account, deviceFid, token);
        fcmTokenRepository.findByAccountAndDeviceFid(account, deviceFid)
                .ifPresentOrElse(
                        existing -> existing.updateToken(token),
                        () -> fcmTokenRepository.save(new FcmToken(account, deviceFid, token))
                );
    }

    @Transactional
    public void changeNotificationEnabled(Account account, String deviceFid, boolean notificationEnabled) {
        FcmToken fcmToken = fcmTokenRepository.findByAccountAndDeviceFid(account, deviceFid)
                .orElseThrow(() -> new NotFoundException(ErrorTag.FCM_TOKEN_NOT_FOUND));
        fcmToken.changeNotificationEnabled(notificationEnabled);
    }

    @Transactional
    public void deleteByAccountAndDeviceFid(Account account, String deviceFid) {
        fcmTokenRepository.deleteByAccountAndDeviceFid(account, deviceFid);
    }

    private void reassignTokenIfOwnedByOther(Account account, String deviceFid, String token) {
        fcmTokenRepository.deleteReassignedToken(token, account, deviceFid);
    }
}
