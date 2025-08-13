package turip.contentplace.controller;

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
import turip.contentplace.controller.dto.response.ContentPlaceDetailResponse;
import turip.contentplace.service.ContentPlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/content-places")
@Tag(name = "ContentPlace", description = "여행 코스 API")
public class ContentPlaceController {

    private final ContentPlaceService contentPlaceService;

    @Operation(
            summary = "여행 상세 조회 api",
            description = "특정 컨텐츠에 대한 여행 코스들을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ContentPlaceDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공적으로 컨텐츠 목록 조회",
                                    value = """
                                            {
                                                "tripDuration": {
                                                    "nights": 0,
                                                    "days": 1
                                                },
                                                "contentPlaceCount": 2,
                                                "contentPlaces": [
                                                    {
                                                        "id": 1,
                                                        "visitDay": 1,
                                                        "visitOrder": 1,
                                                        "place": {
                                                            "id": 1,
                                                            "name": "거북이금고",
                                                            "url": "https://map.naver.com/p/search/거북이금고/place/38623885",
                                                            "address": "부산 해운대구 중동1로 32 지상1층",
                                                            "latitude": 35.162851,
                                                            "longitude": 129.162647,
                                                            "categories": [
                                                                {
                                                                    "name": "음식점"
                                                                }
                                                            ]
                                                        }
                                                    },
                                                    {
                                                        "id": 2,
                                                        "visitDay": 1,
                                                        "visitOrder": 2,
                                                        "place": {
                                                            "id": 2,
                                                            "name": "해운대",
                                                            "url": "https://map.naver.com/p/search/%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5/place/11491806?c=15.00,0,0,0,dh&placePath=/home?entry=bmp&from=map&fromPanelNum=2&timestamp=202507230900&locale=ko&svcName=map_pcv5&searchText=%ED%95%B4%EC%9A%B4%EB%8C%80%20%ED%95%B4%EC%88%98%EC%9A%95%EC%9E%A5",
                                                            "address": "부산 해운대구 해운대해변로 264",
                                                            "latitude": 35.160936,
                                                            "longitude": 129.16004,
                                                            "categories": [
                                                                {
                                                                    "name": "관광지"
                                                                }
                                                            ]
                                                        }
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<ContentPlaceDetailResponse> readContentPlaceDetails(
            @RequestParam(name = "contentId") Long contentId) {
        ContentPlaceDetailResponse response = contentPlaceService.findContentPlaceDetails(contentId);
        return ResponseEntity.ok(response);
    }
}
