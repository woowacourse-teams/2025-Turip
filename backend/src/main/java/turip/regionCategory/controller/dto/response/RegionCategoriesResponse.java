package turip.regionCategory.controller.dto.response;

import java.util.List;

public record RegionCategoriesResponse(
        List<RegionCategoryResponse> regionCategories
) {

    public static RegionCategoriesResponse from(List<RegionCategoryResponse> regionCategories) {
        return new RegionCategoriesResponse(regionCategories);
    }
} 