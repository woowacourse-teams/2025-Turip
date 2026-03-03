package turip.favorite.controller.dto.response;

public record FavoriteFolderExitResponse(boolean isDeleted) {

    public static FavoriteFolderExitResponse of(boolean isDeleted) {
        return new FavoriteFolderExitResponse(isDeleted);
    }
}
