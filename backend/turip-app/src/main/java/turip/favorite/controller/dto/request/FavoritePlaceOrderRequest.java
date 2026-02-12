package turip.favorite.controller.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FavoritePlaceOrderRequest(@JsonProperty("turipPlaceIdsOrder") List<Long> favoritePlaceIdsOrder) {
}
