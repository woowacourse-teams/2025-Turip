package turip.favorite.stream.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import turip.account.domain.Account;
import turip.event.FcmAlertMessage;
import turip.event.FcmAlertService;
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;
import turip.favorite.service.FavoriteFolderAccountService;

@Component
@RequiredArgsConstructor
public class FavoriteFolderEventListener {

    private final FavoriteFolderStreamService favoriteFolderStreamService;
    private final FavoriteFolderAccountService favoriteFolderAccountService;
    private final FcmAlertService fcmAlertService;

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
        List<Account> accounts = favoriteFolderAccountService.findAccountsByFavoriteFolder(folderId);
        List<Account> recipients = accounts.stream()
                .filter(account -> !account.getId().equals(event.accountId()))
                .toList();
        fcmAlertService.sendToAccounts(recipients, FcmAlertMessage.of("새 멤버가 참여했어요", "함께 튜립에 새로운 멤버가 참여했습니다."));
    }
}
