package turip.place.controller.dto.response;

import java.util.List;
import turip.place.domain.Category;
import turip.place.service.MapProvider;
import turip.place.service.PlaceCategoryMapper;

public record CategoryResponse(
        String name
) {
    public static List<CategoryResponse> from(List<Category> categories) {
        return categories.stream()
                .map(category -> {
                    MapProvider provider = MapProvider.getProviderFromCategoryName(category.getName());
                    String parsedCategoryName = PlaceCategoryMapper.parseCategory(category.getName(), provider);
                    return new CategoryResponse(parsedCategoryName);
                })
                .toList();
    }
}
