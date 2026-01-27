package turip.favorite.controller.dto.request;

import java.util.List;

public record FavoritePlaceMultiFolderRequest(
        List<Long> favoriteFolderIds
) {
}
