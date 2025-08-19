package turip.place.controller.dto.response;

import java.util.List;
import turip.place.domain.Category;

public record CategoryResponse(
        String name
) {
    public static List<CategoryResponse> from(List<Category> categories) {
        return categories.stream()
                .map(category -> new CategoryResponse(category.getName()))
                .toList();
    }
}
