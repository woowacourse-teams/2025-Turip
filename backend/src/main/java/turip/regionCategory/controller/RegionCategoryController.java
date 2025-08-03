package turip.regionCategory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.regionCategory.controller.dto.response.RegionCategoriesResponse;
import turip.regionCategory.service.RegionCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regionCategories")
public class RegionCategoryController {

    private final RegionCategoryService regionCategoryService;

    @GetMapping
    public ResponseEntity<RegionCategoriesResponse> readRegionCategories(
            @RequestParam(name = "isKorea") boolean isKorea) {
        RegionCategoriesResponse response = regionCategoryService.getRegionCategoriesByCountryType(isKorea);
        return ResponseEntity.ok(response);
    }
}
