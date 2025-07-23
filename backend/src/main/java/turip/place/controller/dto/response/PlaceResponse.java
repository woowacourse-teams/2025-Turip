package turip.place.controller.dto.response;

import java.util.List;
import turip.place.domain.Place;
import turip.placecategory.domain.PlaceCategory;

public record PlaceResponse(
        Long id,
        String name,
        String url,
        String address,
        double latitude,
        double longitude,
        List<CategoryResponse> categories
) {
    public static PlaceResponse from(Place place) {
        return new PlaceResponse(
                place.getId(),
                place.getName(),
                place.getUrl(),
                place.getAddress(),
                place.getLatitude(),
                place.getLongitude(),
                CategoryResponse.from(
                        place.getPlaceCategories().stream()
                                .map(PlaceCategory::getCategory)
                                .toList()
                )
        );
    }
}
