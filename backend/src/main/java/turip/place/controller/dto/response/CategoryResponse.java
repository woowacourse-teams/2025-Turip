package turip.place.controller.dto.response;

import java.util.List;

public record CategoryResponse(
        String name
) {
    public static List<CategoryResponse> from(List<String> categories) {
        return categories.stream()
                .map(CategoryResponse::new)
                .toList();
    }
}
