package turip.favorite.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FavoritePlaceBatchCreateRequest(
        @JsonProperty("turipId") Long favoriteFolderId,
        List<Long> placeIds
) {
}
