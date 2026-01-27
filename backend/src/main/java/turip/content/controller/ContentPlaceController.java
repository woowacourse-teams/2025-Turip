package turip.content.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Account;
import turip.auth.resolver.AuthAccount;
import turip.common.exception.ErrorResponse;
import turip.content.controller.dto.response.place.ContentPlaceDetailResponse;
import turip.content.service.ContentPlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/contents")
@Tag(name = "ContentPlace", description = "컨텐츠 장소 API")
public class ContentPlaceController {

    private final ContentPlaceService contentPlaceService;

    @Operation(
            summary = "컨텐츠 상세 조회 api",
            description = "특정 컨텐츠에 대한 장소들을 조회한다."
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
                                              "contentPlaces": [
                                                {
                                                  "id": 1,
                                                  "visitDay": 1,
                                                  "visitOrder": 1,
                                                  "place": {
                                                    "id": 1,
                                                    "name": "엄용백돼지국밥 해운대점",
                                                    "url": "https://place.map.kakao.com/995188706",
                                                    "address": "부산 해운대구 우동 635-6",
                                                    "latitude": 35.1607250250963,
                                                    "longitude": 129.158371165903,
                                                    "categories": [
                                                      {
                                                        "name": "음식점 > 🍚 한식 > 🥣 국밥"
                                                      }
                                                    ]
                                                  },
                                                  "timeLine": "01:22",
                                                  "isFavoritePlace": false
                                                },
                                                {
                                                  "id": 2,
                                                  "visitDay": 1,
                                                  "visitOrder": 2,
                                                  "place": {
                                                    "id": 2,
                                                    "name": "해운대해수욕장",
                                                    "url": "https://place.map.kakao.com/7913306",
                                                    "address": "부산 해운대구 우동",
                                                    "latitude": 35.1585232170784,
                                                    "longitude": 129.159854668484,
                                                    "categories": [
                                                      {
                                                        "name": "여행 > 📍 관광,명소 > 🏖️ 해수욕장,해변"
                                                      }
                                                    ]
                                                  },
                                                  "timeLine": "02:00",
                                                  "isFavoritePlace": false
                                                },
                                                {
                                                  "id": 3,
                                                  "visitDay": 2,
                                                  "visitOrder": 1,
                                                  "place": {
                                                    "id": 3,
                                                    "name": "아난티 앳 부산 빌라쥬",
                                                    "url": "https://place.map.kakao.com/302024514",
                                                    "address": "부산 기장군 기장읍 시랑리 733",
                                                    "latitude": 35.1987678492266,
                                                    "longitude": 129.221311209222,
                                                    "categories": [
                                                      {
                                                        "name": "여행 > 🛏️ 숙박 > 🏨 호텔"
                                                      }
                                                    ]
                                                  },
                                                  "timeLine": "02:20",
                                                  "isFavoritePlace": false
                                                }
                                              ],
                                              "contentPlaceCount": 3,
                                              "tripDuration": {
                                                "nights": 1,
                                                "days": 2
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "access token expired",
                                            summary = "만료된 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_EXPIRED",
                                                    	"message": "access token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature access token",
                                            summary = "서명값이 올바르지 않은 access token",
                                            value = """
                                                    {
                                                    	"tag": "ACCESS_TOKEN_SIGNATURE_INVALID",
                                                    	"message": "access token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "unauthorized",
                                            summary = "알 수 없는 이유로 인증 실패",
                                            value = """
                                                    {
                                                    	"tag": "UNAUTHORIZED",
                                                    	"message": "토큰 기반 인증에 실패했습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "content_not_found",
                                    summary = "컨텐츠를 찾을 수 없음",
                                    value = """
                                            {
                                                "tag": "CONTENT_NOT_FOUND",
                                                "message": "컨텐츠를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{contentId}/places")
    public ResponseEntity<ContentPlaceDetailResponse> readContentPlaceDetails(
            @Parameter(hidden = true) @AuthAccount Account account,
            @PathVariable Long contentId) {
        ContentPlaceDetailResponse response = contentPlaceService.findContentPlaceDetails(account, contentId);
        return ResponseEntity.ok(response);
    }
}
