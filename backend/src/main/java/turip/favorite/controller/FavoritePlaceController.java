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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.AuthMember;
import turip.auth.MemberResolvePolicy;
import turip.common.exception.ErrorResponse;
import turip.favorite.controller.dto.request.FavoritePlaceOrderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlaceResponse;
import turip.favorite.controller.dto.response.FavoriteFolderWithFavoriteStatusResponse.FavoritePlacesWithDetailPlaceInformationResponse;
import turip.favorite.service.FavoritePlaceService;
import turip.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorites/places")
@Tag(name = "FavoritePlace", description = "장소 찜 API")
public class FavoritePlaceController {

    private final FavoritePlaceService favoritePlaceService;

    @Operation(
            summary = "장소 찜 생성 api",
            description = "장소를 찜한다."
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
                                    summary = "장소 찜 생성 성공",
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
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_folder_owner",
                                            summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                            value = """
                                                    {
                                                        "tag" : "FORBIDDEN"
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
                                            name = "folder_not_found",
                                            summary = "favoriteFolderId에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_FOLDER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_PLACE_NOT_FOUND"
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
                                            name = "already_favorite",
                                            summary = "이미 해당 폴더에 찜한 상태인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_PLACE_IN_FOLDER_CONFLICT"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoritePlaceResponse> create(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        FavoritePlaceResponse response = favoritePlaceService.create(member, favoriteFolderId, placeId);
        return ResponseEntity.created(URI.create("/favorites/places/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "장소 찜 폴더의 장소 찜 조회 api",
            description = "장소 찜 폴더의 장소 찜 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoritePlacesWithDetailPlaceInformationResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더의 장소 찜 목록 조회 성공",
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
                                                        }
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
                                                        }
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
                                            name = "folder_not_found",
                                            summary = "favoriteFolderId에 대한 폴더가 존재하지 않는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_FOLDER_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @GetMapping
    public ResponseEntity<FavoritePlacesWithDetailPlaceInformationResponse> readAllByFolder(
            @RequestParam("favoriteFolderId") Long favoriteFolderId) {
        FavoritePlacesWithDetailPlaceInformationResponse response = favoritePlaceService.findAllByFolder(
                favoriteFolderId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 내의 장소 찜 정렬 순서 변경 api",
            description = "장소 찜 폴더의 장소 찜들의 정렬 순서를 변경한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_folder_owner",
                                            summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                            value = """
                                                    {
                                                        "tag" : "FORBIDDEN"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "favorite_place_not_belongs_to_folder",
                                            summary = "다른 폴더의 favoritePlaceId가 포함된 경우",
                                            value = """
                                                    {
                                                        "tag" : "FORBIDDEN"
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
                                            name = "member_not_found",
                                            summary = "device-fid에 대한 회원을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "MEMBER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "id에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_FOLDER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "favorite_place_not_found",
                                            summary = "favoritePlaceId에 대한 찜한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_PLACE_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/favorite-order")
    public ResponseEntity<Void> updatePlaceOrder(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.REQUIRED) Member member,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestBody FavoritePlaceOrderRequest request
    ) {
        favoritePlaceService.updatePlaceOrder(member, favoriteFolderId, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "장소 찜 삭제 api",
            description = "장소 찜을 취소한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "성공 예시"
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "not_folder_owner",
                                            summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                            value = """
                                                    {
                                                        "tag" : "FORBIDDEN"
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
                                            name = "member_not_found",
                                            summary = "deviceFid에 대한 회원을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "MEMBER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_not_found",
                                            summary = "favoriteFolderId에 대한 폴더를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_FOLDER_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "place_not_found",
                                            summary = "placeId에 대한 장소를 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                        "tag" : "PLACE_NOT_FOUND"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "favorite_place_not_found",
                                            summary = "해당 폴더에 장소 찜이 되어있지 않은 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_PLACE_NOT_FOUND"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.REQUIRED) Member member,
            @RequestParam("favoriteFolderId") Long favoriteFolderId,
            @RequestParam("placeId") Long placeId
    ) {
        favoritePlaceService.remove(member, favoriteFolderId, placeId);
        return ResponseEntity.noContent().build();
    }
}
