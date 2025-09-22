package turip.favorite.controller.dto.request;

import java.util.List;

public record FavoritePlaceOrderRequest(
        List<Long> favoritePlaceIdsOrder
) {
}
