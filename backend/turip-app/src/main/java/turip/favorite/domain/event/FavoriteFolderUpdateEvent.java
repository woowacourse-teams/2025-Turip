package turip.favorite.domain.event;

public record FavoriteFolderUpdateEvent(
        Long favoriteFolderId,
        ActionType actionType
) {
    public static FavoriteFolderUpdateEvent of(Long favoriteFolderId, ActionType actionType) {
        return new FavoriteFolderUpdateEvent(favoriteFolderId, actionType);
    }
}
