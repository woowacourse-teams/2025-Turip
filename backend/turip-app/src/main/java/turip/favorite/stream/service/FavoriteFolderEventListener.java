package turip.favorite.stream.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import turip.account.notification.FcmNotificationService;
import turip.account.notification.NotificationType;
import turip.favorite.domain.FavoriteFolder;
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;
import turip.favorite.repository.FavoriteFolderRepository;
import turip.favorite.service.FavoriteFolderAccountService;

@Slf4j
@Component
@RequiredArgsConstructor
public class FavoriteFolderEventListener {

    private final FavoriteFolderStreamService favoriteFolderStreamService;
    private final FavoriteFolderAccountService favoriteFolderAccountService;
    private final FavoriteFolderRepository favoriteFolderRepository;
    private final FcmNotificationService fcmNotificationService;

    @Async("sseEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFavoriteFolderUpdateEvent(FavoriteFolderUpdateEvent event) {
        Long folderId = event.favoriteFolderId();
        ActionType action = event.actionType();

        if (action == ActionType.MEMBER_JOINED || action == ActionType.MEMBER_EXITED) {
            favoriteFolderStreamService.sendMemberUpdateEvents(folderId, action);
            return;
        }

        if (action == ActionType.FOLDER_DELETED) {
            favoriteFolderStreamService.closeAllEmittersForFolder(folderId);
            return;
        }

        favoriteFolderStreamService.sendFolderUpdateEvents(folderId, action);
    }

    @Async("fcmEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFavoriteFolderJoinAlertEvent(FavoriteFolderUpdateEvent event) {
        ActionType action = event.actionType();
        if (action != ActionType.MEMBER_JOINED) {
            return;
        }

        Long folderId = event.favoriteFolderId();
        FavoriteFolder favoriteFolder = favoriteFolderRepository.findById(folderId)
                .orElse(null);

        if (favoriteFolder == null) {
            log.warn("폴더를 찾을 수 없어 알림 전송을 건너뜁니다. folderId={}", folderId);
            return;
        }

        List<Long> accountIds = favoriteFolderAccountService.findAccountIdsByFavoriteFolder(folderId);
        List<Long> recipients = accountIds.stream()
                .filter(accountId -> !accountId.equals(event.accountId()))
                .toList();
        if (recipients.isEmpty()) {
            return;
        }

        fcmNotificationService.sendToAccounts(
                recipients,
                NotificationType.NEW_MEMBER_JOINED.createMessage(favoriteFolder.getName())
        );
    }
}
