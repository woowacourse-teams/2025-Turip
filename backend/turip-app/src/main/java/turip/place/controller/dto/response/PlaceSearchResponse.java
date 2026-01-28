package turip.place.controller.dto.response;

import java.util.List;
import turip.place.domain.PlaceSearchProvider;

public record PlaceSearchResponse(
        PlaceSearchProvider provider,
        List<PlaceSearchItem> items
) {
    public record PlaceSearchItem(
            String externalId,
            String name,
            String url,
            String address,
            double latitude,
            double longitude,
            String categoryName
    ) {
    }
}
