package turip.favorite.domain.event;

import jakarta.annotation.Nullable;

public record FavoriteFolderUpdateEvent(
        Long favoriteFolderId,
        ActionType actionType,
        @Nullable Long accountId
) {
    public static FavoriteFolderUpdateEvent of(Long favoriteFolderId, ActionType actionType) {
        return new FavoriteFolderUpdateEvent(favoriteFolderId, actionType, null);
    }

    public static FavoriteFolderUpdateEvent of(Long favoriteFolderId, ActionType actionType, Long accountId) {
        return new FavoriteFolderUpdateEvent(favoriteFolderId, actionType, accountId);
    }
}
