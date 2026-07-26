package turip.region.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import turip.region.controller.dto.response.RegionCategoriesResponse;
import turip.region.controller.dto.response.RelatedSpotsResponse;
import turip.region.domain.DomesticRegionCategory;
import turip.region.service.RegionCategoryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
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
    @GetMapping("/region-categories")
    public ResponseEntity<RegionCategoriesResponse> readRegionCategories(
            @RequestParam(name = "isKorea") boolean isDomestic) {
        RegionCategoriesResponse response = regionCategoryService.findRegionCategoriesByCountryType(isDomestic);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "지역별 연관 관광지 조회",
            description = "국내 지역 카테고리를 기반으로 연관 관광지 목록을 조회합니다. 한국관광공사 API 데이터를 기반으로 카테고리별로 그룹화하여 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RelatedSpotsResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "서울 지역 연관 관광지 조회 성공",
                                    value = """
                                            {
                                                "relatedSpots": [
                                                    {
                                                        "category": "관광지",
                                                        "spots": ["종묘", "북촌한옥마을", "경복궁", "창덕궁"]
                                                    },
                                                    {
                                                        "category": "쇼핑",
                                                        "spots": ["명동", "동대문시장"]
                                                    },
                                                    {
                                                        "category": "문화시설",
                                                        "spots": ["국립중앙박물관", "서울역사박물관"]
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/related-spots")
    public ResponseEntity<RelatedSpotsResponse> getRelatedSpots(
            @Parameter(description = "지역 카테고리 (서울, 부산, 제주, 인천, 대전, 전주, 강릉, 속초, 경주)", required = true, example = "서울")
            @RequestParam(name = "regionCategory") DomesticRegionCategory regionCategory
    ) {
        RelatedSpotsResponse response = regionCategoryService.findRelatedSpotsByRegionCategory(regionCategory);
        return ResponseEntity.ok(response);
    }
}
