package turip.favorite.stream.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import turip.favorite.domain.event.FavoriteFolderUpdateEvent;

@Component
@RequiredArgsConstructor
public class FavoriteFolderEventListener {

    private final FavoriteFolderStreamService favoriteFolderStreamService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleFolderUpdate(FavoriteFolderUpdateEvent event) {
        favoriteFolderStreamService.sendFolderUpdateEvents(
                event.favoriteFolderId(),
                event.actionType()
        );
    }
}
