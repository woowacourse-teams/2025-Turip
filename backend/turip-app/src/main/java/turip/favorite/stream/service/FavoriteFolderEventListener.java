package turip.favorite.stream.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import turip.favorite.domain.event.ActionType;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;

@Component
@RequiredArgsConstructor
public class FavoriteFolderEventListener {

    private final FavoriteFolderStreamService favoriteFolderStreamService;

    @Async("sseEventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFavoriteFolderUpdateEvent(FavoriteFolderUpdateEvent event) {
        Long folderId = event.favoriteFolderId();
        ActionType action = event.actionType();

        if (action == ActionType.MEMBER_JOINED || action == ActionType.MEMBER_EXITED) {
            favoriteFolderStreamService.sendMemberUpdateEvents(folderId, action);
            return;
        }

        favoriteFolderStreamService.sendFolderUpdateEvents(folderId, action);
    }
}
