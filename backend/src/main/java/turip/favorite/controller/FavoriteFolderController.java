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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.AuthMember;
import turip.auth.MemberResolvePolicy;
import turip.common.exception.ErrorResponse;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.service.FavoriteFolderService;
import turip.member.domain.Member;

@RestController
@RequiredArgsConstructor
@RequestMapping("/favorite-folders")
@Tag(name = "FavoriteFolder", description = "장소 찜 폴더 API")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    @Operation(
            summary = "장소 찜 폴더 생성 api",
            description = "장소 찜 폴더를 생성한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 1,
                                                "name": "뭉치가 가고싶은 맛집들",
                                                "isDefault": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "folder_name_blank",
                                            summary = "장소 찜 폴더 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_BLANK"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_name_length_over",
                                            summary = "장소 찜 폴더 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_TOO_LONG"
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
                                            name = "already_exists_folder_name",
                                            summary = "같은 이름의 폴더가 이미 존재하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_CONFLICT"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoriteFolderResponse> create(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member,
            @RequestBody FavoriteFolderRequest request) {
        FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, member);
        return ResponseEntity.created(URI.create("/favorite-folders/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "장소 찜 폴더 조회 api",
            description = "특정 회원의 장소 찜 폴더 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersWithPlaceCountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 조회 성공",
                                    value = """
                                            {
                                                "favoriteFolders": [
                                                    {
                                                        "id": 5,
                                                        "memberId": 8,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "placeCount": 0
                                                    },
                                                    {
                                                        "id": 6,
                                                        "memberId": 8,
                                                        "name": "뭉치가 가고싶은 맛집들",
                                                        "isDefault": false,
                                                        "placeCount": 0
                                                    }
                                                ]
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public ResponseEntity<FavoriteFoldersWithPlaceCountResponse> readAllByMember(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member) {
        FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByMember(member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 목록과 장소 찜 여부 조회 api",
            description = "특정 회원의 장소 찜 폴더 목록과, 특정 장소에 대한 찜 여부를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersWithFavoriteStatusResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 조회 성공",
                                    value = """
                                            {
                                                "favoriteFolders": [
                                                    {
                                                        "id": 5,
                                                        "memberId": 1,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "isFavoritePlace": true
                                                    },
                                                    {
                                                        "id": 6,
                                                        "memberId": 1,
                                                        "name": "잠실 맛집들",
                                                        "isDefault": false,
                                                        "isFavoritePlace": false
                                                    }
                                                ]
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
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "placeId에 대한 장소가 존재하지 않는 경우",
                                    value = """
                                            {
                                                "tag": "PLACE_NOT_FOUND"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/favorite-status")
    public ResponseEntity<FavoriteFoldersWithFavoriteStatusResponse> readAllWithFavoriteStatusByDeviceId(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.CREATE_IF_ABSENT) Member member,
            @RequestParam("placeId") Long placeId) {
        FavoriteFoldersWithFavoriteStatusResponse response = favoriteFolderService.findAllWithFavoriteStatusByDeviceId(
                member, placeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 이름 수정 api",
            description = "장소 찜 폴더의 이름을 수정한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "장소 찜 폴더 이름 수정 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 1,
                                                "name": "수정된 폴더명",
                                                "isDefault": false
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "folder_name_blank",
                                            summary = "장소 찜 폴더 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_BLANK"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_name_length_over",
                                            summary = "장소 찜 폴더 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_TOO_LONG"
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "is_default_folder",
                                            summary = "장소 찜 폴더가 기본 폴더인 경우",
                                            value = """
                                                    {
                                                        "tag": "IS_DEFAULT_FAVORITE_FOLDER"
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
                                            name = "folder_name_already_exists",
                                            summary = "중복되는 폴더 이름이 존재하는 경우",
                                            value = """
                                                    {
                                                        "tag" : "FAVORITE_FOLDER_NAME_CONFLICT"
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PatchMapping("/{favoriteFolderId}")
    public ResponseEntity<FavoriteFolderResponse> updateName(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.REQUIRED) Member member,
            @PathVariable Long favoriteFolderId,
            @RequestBody FavoriteFolderNameRequest request
    ) {
        FavoriteFolderResponse response = favoriteFolderService.updateName(member, favoriteFolderId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "장소 찜 폴더 삭제 api",
            description = "장소 찜 폴더를 삭제한다."
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
                                    name = "is_default_folder",
                                    summary = "삭제하려는 폴더가 기본 폴더인 경우",
                                    value = """
                                            {
                                                "tag" : "IS_DEFAULT_FAVORITE_FOLDER"
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
                            examples = @ExampleObject(
                                    name = "not_folder_owner",
                                    summary = "폴더 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
                                    value = """
                                            {
                                                "tag" : "FORBIDDEN"
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
                                    )
                            }
                    )
            )
    })
    @DeleteMapping("/{favoriteFolderId}")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthMember(policy = MemberResolvePolicy.REQUIRED) Member member,
            @PathVariable Long favoriteFolderId) {
        favoriteFolderService.remove(member, favoriteFolderId);
        return ResponseEntity.noContent().build();
    }
}
