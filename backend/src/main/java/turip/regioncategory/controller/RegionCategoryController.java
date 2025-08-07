package turip.regioncategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "RegionCategory", description = "지역 카테고리 API")
public class RegionCategoryController {

    private final RegionCategoryService regionCategoryService;

    @Operation(
            summary = "국내 해외 지역 조회 api",
            description = "국내와 해외의 지역 카테고리 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegionCategoriesResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 카테고리 조회",
                                    value = """
                                            {
                                                "regionCategories": [
                                                    {
                                                        "regionCategoryName": "서울",
                                                        "country": {
                                                            "id": 1,
                                                            "countryName": "대한민국",
                                                            "countryImageUrl": "http://koreaImage.com"
                                                        },
                                                        "regionCategoryImageUrl": "http://youtube.com"
                                                    },
                                                    {
                                                        "regionCategoryName": "부산",
                                                        "country": {
                                                            "id": 1,
                                                            "countryName": "대한민국",
                                                            "countryImageUrl": "http://koreaImage.com"
                                                        },
                                                        "regionCategoryImageUrl": "http://youtube.com"
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<RegionCategoriesResponse> readRegionCategories(
            @RequestParam(name = "isKorea") boolean isDomestic) {
        RegionCategoriesResponse response = regionCategoryService.findRegionCategoriesByCountryType(isDomestic);
        return ResponseEntity.ok(response);
    }
}
