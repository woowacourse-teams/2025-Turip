package turip.favorite.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record FavoritePlaceBatchCreateRequest(
        @JsonProperty("turipId")
        @NotNull(message = "올바르지 않은 요청입니다.")
        Long favoriteFolderId,

        @NotNull(message = "올바르지 않은 요청입니다.")
        @Size(max = 70, message = "한 번에 추가할 수 있는 장소 개수를 초과했습니다.")
        List<@NotNull(message = "올바르지 않은 요청입니다.") Long> placeIds
) {
}
