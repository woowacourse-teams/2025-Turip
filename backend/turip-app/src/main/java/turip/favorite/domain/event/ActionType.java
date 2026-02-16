package turip.favorite.domain.event;

public enum ActionType {
    // FavoritePlace 관련
    PLACE_REORDERED,
    PLACE_ADDED,
    PLACE_DELETED,

    // FavoriteFolder 관련
    FOLDER_NAME_CHANGED,
    FOLDER_DELETED,
    FOLDER_PLACE_CHANGED,

    // Member 관련
    MEMBER_JOINED,
    MEMBER_EXITED,
    ;
}
