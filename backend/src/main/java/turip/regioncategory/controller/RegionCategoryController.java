package turip.regioncategory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.regioncategory.controller.dto.response.RegionCategoriesResponse;
import turip.regioncategory.service.RegionCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/region-categories")
public class RegionCategoryController {

    private final RegionCategoryService regionCategoryService;

    @GetMapping
    public ResponseEntity<RegionCategoriesResponse> readRegionCategories(
            @RequestParam(name = "isKorea") boolean isDomestic) {
        RegionCategoriesResponse response = regionCategoryService.findRegionCategoriesByCountryType(isDomestic);
        return ResponseEntity.ok(response);
    }
}
