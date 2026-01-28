package turip.favorite.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Account;
import turip.auth.resolver.AuthAccount;
import turip.common.exception.ErrorResponse;
import turip.favorite.controller.dto.request.FavoritePlaceMultiFolderRequest;
import turip.favorite.controller.dto.request.FavoritePlaceOrderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlacesWithPlaceDetailResponse;
import turip.favorite.controller.dto.response.FavoritePlaceCountResponse;
import turip.favorite.service.FavoritePlaceService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/turips/places")
@Tag(name = "TuripPlace", description = "튜립 장소 API")
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @Operation(
            summary = "튜립 장소 추가 api",
            description = "내 튜립에 장소를 추가한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePlaceResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 장소 추가 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "favoriteFolderId": 3,
                                                "placeId": 5
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
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "not_turip_owner",
                                    summary = "튜립 소유자가 아닌 사용자가 요청한 경우",
                                    value = """
                                            {
                                                "tag": "FORBIDDEN",
                                                "message": "접근 권한이 없습니다."
                                            }
                                            """
                            )

                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "turip_not_found",
                                            summary = "turipId에 대한 폴더(튜립)를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                        "message": "찜폴더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "PLACE_NOT_FOUND",
                                                        "message": "장소를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "already_turip_place",
                                            summary = "이미 해당 튜립에 추가된 장소인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_PLACE_IN_FOLDER_CONFLICT",
                                                        "message": "해당 폴더에 이미 찜한 장소입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoritePlaceResponse> create(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam("turipId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        FavoritePlaceResponse response = favoritePlaceService.create(account, favoriteFolderId, placeId);
        return ResponseEntity.created(URI.create("/turips/places/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "튜립 내 장소 조회 api",
            description = "튜립에 추가한 장소들을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePlacesWithPlaceDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 내 장소 목록 조회 성공",
                                    value = """
                                            {
                                                "favoritePlaces": [
                                                    {
                                                        "id": 1,
                                                        "place": {
                                                            "id": 5,
                                                            "name": "니넨자카",
                                                            "url": "https://maps.google.com/?cid=16622321643655642677",
                                                            "address": "일본 〒605-0826 Kyoto, Higashiyama Ward, Masuyacho, 清水2丁目",
                                                            "latitude": 34.9981744,
                                                            "longitude": 135.7808578,
                                                            "categories": [
                                                                {
                                                                    "name": "tourist_attraction"
                                                                }
                                                            ]
                                                        },
                                                        "favoriteOrder": 1
                                                    },
                                                    {
                                                        "id": 2,
                                                        "place": {
                                                            "id": 11,
                                                            "name": "기막힌 닭",
                                                            "url": "https://place.map.kakao.com/1423754025",
                                                            "address": "강원특별자치도 양양군 현남면 인구리 1-51",
                                                            "latitude": 37.9705767151489,
                                                            "longitude": 128.761802013276,
                                                            "categories": [
                                                                {
                                                                    "name": "음식점 > 한식 > 육류,고기 > 닭요리"
                                                                }
                                                            ]
                                                        },
                                                        "favoriteOrder": 2
                                                    }
                                                ],
                                                "favoritePlaceCount": 2
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "turip_not_found",
                                            summary = "turipId에 대한 튜립이 존재하지 않는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                        "message": "찜폴더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<FavoritePlacesWithPlaceDetailResponse> readAllByFolder(
            @RequestParam("turipId") Long favoriteFolderId) {
        FavoritePlacesWithPlaceDetailResponse response = favoritePlaceService.findAllByFolder(
                favoriteFolderId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 다중 업데이트 api",
            description = "특정 장소에 대해 선택된 튜립 목록을 일괄 업데이트한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(type = "array", implementation = FavoritePlaceResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 업데이트 성공",
                                    value = """
                                            [
                                                {
                                                    "id": 10,
                                                    "favoriteFolderId": 1,
                                                    "placeId": 123
                                                },
                                                {
                                                    "id": 11,
                                                    "favoriteFolderId": 2,
                                                    "placeId": 123
                                                }
                                            ]
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
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_turip_owner",
                                            summary = "튜립 소유자가 아닌 경우",
                                            value = """
                                                    {
                                                        "tag": "FORBIDDEN",
                                                        "message": "접근 권한이 없습니다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "turip_not_found",
                                            summary = "turipId에 대한 튜립을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                        "message": "찜폴더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "장소가 존재하지 않는 경우",
                                            value = """
                                                    {
                                                        "tag": "PLACE_NOT_FOUND",
                                                        "message": "장소를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PutMapping("/{placeId}")
    public ResponseEntity<List<FavoritePlaceResponse>> updateFavoriteFolders(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestBody FavoritePlaceMultiFolderRequest request,
            @PathVariable Long placeId
    ) {
        List<FavoritePlaceResponse> response = favoritePlaceService.updateFavoriteFolders(account,
                request.favoriteFolderIds(), placeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립의 장소 수 조회 api",
            description = "튜립의 장소 수를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePlaceCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립의 장소 수 조회 성공",
                                    value = """
                                            {
                                               "count": 5
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
            )
    })
    @GetMapping("/count")
    public ResponseEntity<FavoritePlaceCountResponse> readCountByAccount(
            @Parameter(hidden = true) @AuthAccount Account account) {
        FavoritePlaceCountResponse response = favoritePlaceService.countByAccount(account);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 내의 장소 정렬 순서 변경 api",
            description = "튜립 장소들의 정렬 순서를 변경한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "turip_place_turip_mismatch",
                                    summary = "장소가 해당 튜립에 존재하지 않는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_PLACE_FOLDER_MISMATCH",
                                                "message": "장소 찜이 해당 폴더에 존재하지 않습니다."
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
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "not_turip_owner",
                                    summary = "튜립 소유자가 아닌 경우",
                                    value = """
                                            {
                                                "tag": "FORBIDDEN",
                                                "message": "접근 권한이 없습니다."
                                            }
                                            """
                            )

                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "turip_not_found",
                                            summary = "id에 대한 튜립을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                        "message": "찜폴더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "turip_place_not_found",
                                            summary = "favoritePlaceId에 대한 튜립 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_PLACE_NOT_FOUND",
                                                        "message": "찜한 장소를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/turip-order")
    public ResponseEntity<Void> updatePlaceOrder(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam("turipId") Long favoriteFolderId,
            @RequestBody FavoritePlaceOrderRequest request
    ) {
        favoritePlaceService.updatePlaceOrder(account, favoriteFolderId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "튜립의 장소 삭제 api",
            description = "튜립에서 장소를 제거한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
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
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_turip_owner",
                                            summary = "튜립 소유자가 아닌 경우",
                                            value = """
                                                    {
                                                        "tag": "FORBIDDEN",
                                                        "message": "접근 권한이 없습니다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "turip_not_found",
                                            summary = "turipId에 대한 튜립을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                        "message": "찜폴더를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag": "PLACE_NOT_FOUND",
                                                        "message": "장소를 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "turip_place_not_found",
                                            summary = "해당 튜립에 장소가 추가되어있지 않은 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_PLACE_NOT_FOUND",
                                                        "message": "찜한 장소를 찾을 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam("turipId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        favoritePlaceService.remove(account, favoriteFolderId, placeId);
        return ResponseEntity.noContent().build();
    }
}
