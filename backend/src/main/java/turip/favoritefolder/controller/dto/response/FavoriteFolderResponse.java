package turip.favoritefolder.controller.dto.response;

import turip.favoritefolder.domain.FavoriteFolder;

public record FavoriteFolderResponse(
        Long id,
        Long memberId,
        String name,
        boolean isDefault
) {

    public static FavoriteFolderResponse from(FavoriteFolder favoriteFolder) {
        return new FavoriteFolderResponse(
                favoriteFolder.getId(),
                favoriteFolder.getMember().getId(),
                favoriteFolder.getName(),
                favoriteFolder.isDefault()
        );
    }
}
