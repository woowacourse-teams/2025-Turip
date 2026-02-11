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
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.auth.resolver.AuthAccount;
import turip.auth.resolver.AuthMember;
import turip.common.exception.ErrorResponse;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFolderExitResponse;
import turip.favorite.controller.dto.response.FavoriteFolderJoinResponse;
import turip.favorite.controller.dto.response.FavoriteFolderMembersResponse;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersDetailResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.service.FavoriteFolderService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/turips")
@Tag(name = "Turip", description = "튜립 API")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;

    @Operation(
            summary = "튜립 생성 api",
            description = "나의 튜립을 생성한다."
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
                                    summary = "튜립 생성 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "accountId": 1,
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
                                            name = "turip_name_blank",
                                            summary = "튜립 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_BLANK",
                                                        "message": "찜폴더 이름은 비워둘 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "turip_name_length_over",
                                            summary = "튜립 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_TOO_LONG",
                                                        "message": "찜폴더 이름의 최대 길이를 초과했습니다."
                                                    }
                                                    """
                                    )
                            }
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
                    responseCode = "409",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "already_exists_turip_name",
                                            summary = "같은 이름의 튜립이 이미 존재하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_CONFLICT",
                                                        "message": "이미 존재하는 찜폴더 이름입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping
    public ResponseEntity<FavoriteFolderResponse> create(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestBody FavoriteFolderRequest request) {
        FavoriteFolderResponse response = favoriteFolderService.createCustomFavoriteFolder(request, account);
        return ResponseEntity.created(URI.create("/api/v1/turips/" + response.id()))
                .body(response);
    }

    @Operation(
            summary = "튜립 참여 api",
            description = "회원을 공유 폴더에 참여시킨다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderJoinResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 참여 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "turipId": 1,
                                                "isShared": true,
                                                "accountId": 1
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
                                            name = "personal folder not valid",
                                            summary = "개인 찜폴더인 경우",
                                            value = """
                                                    {
                                                    	"tag": "PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                    	"message": "개인 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "default folder not valid",
                                            summary = "기본 찜폴더인 경우",
                                            value = """
                                                    {
                                                    	"tag": "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                    	"message": "기본 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                    name = "guest_forbidden",
                                    summary = "게스트가 해당 기능에 접근하려는 경우",
                                    value = """
                                            {
                                                "tag": "GUEST_FORBIDDEN",
                                                "message": "게스트 계정이 접근할 수 없는 기능입니다."
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
                                    name = "turip_not_found",
                                    summary = "turipId에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/{turipId}/join")
    public ResponseEntity<FavoriteFolderJoinResponse> join(
            @Parameter(hidden = true) @AuthMember Member member,
            @PathVariable("turipId") Long favoriteFolderId) {
        FavoriteFolderJoinResponse response = favoriteFolderService.joinMember(favoriteFolderId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 조회 api",
            description = "특정 회원의 튜립 목록을 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFoldersDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 조회 성공",
                                    value = """
                                            {
                                                "turips": [
                                                    {
                                                        "id": 5,
                                                        "accountId": 8,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "placeCount": 0,
                                                        "memberCount": 1,
                                                        "isShared": false
                                                    },
                                                    {
                                                        "id": 6,
                                                        "accountId": 8,
                                                        "name": "뭉치가 가고싶은 맛집들",
                                                        "isDefault": false,
                                                        "placeCount": 0,
                                                        "memberCount": 5,
                                                        "isShared": true
                                                    }
                                                ]
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
    @GetMapping
    public ResponseEntity<FavoriteFoldersDetailResponse> readAllByMember(
            @Parameter(hidden = true) @AuthAccount Account account) {
        FavoriteFoldersDetailResponse response = favoriteFolderService.findAllByAccount(account);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 목록과 튜립 장소 여부 조회 api",
            description = "특정 회원의 튜립 목록과, 특정 장소의 튜립 포함 여부를 조회한다."
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
                                    summary = "튜립 조회 성공",
                                    value = """
                                            {
                                                "turips": [
                                                    {
                                                        "id": 5,
                                                        "accountId": 1,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "isTuripPlace": true
                                                    },
                                                    {
                                                        "id": 6,
                                                        "accountId": 1,
                                                        "name": "잠실 맛집들",
                                                        "isDefault": false,
                                                        "isTuripPlace": false
                                                    }
                                                ]
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
                                    name = "place_not_found",
                                    summary = "placeId에 대한 장소가 존재하지 않는 경우",
                                    value = """
                                            {
                                                "tag": "PLACE_NOT_FOUND",
                                                "message": "장소를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/turip-status")
    public ResponseEntity<FavoriteFoldersWithFavoriteStatusResponse> readAllWithFavoriteStatusByDeviceId(
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestParam("placeId") Long placeId) {
        FavoriteFoldersWithFavoriteStatusResponse response = favoriteFolderService.findAllWithFavoriteStatusByAccountId(
                account, placeId);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 상세 조회 api",
            description = "특정 튜립의 상세 정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderDetailResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 상세 조회 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "accountId": 1,
                                                "name": "튜립 부산",
                                                "isDefault": false,
                                                "placeCount": 2,
                                                "isShared": true,
                                                "memberCount": 4
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
                                    name = "forbidden",
                                    summary = "해당 계정이 튜립에 대한 접근 권한이 없는 경우",
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
                            examples = @ExampleObject(
                                    name = "turip_not_found",
                                    summary = "turipId에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/{turipId}")
    public ResponseEntity<FavoriteFolderDetailResponse> readById(
            @Parameter(hidden = true) @AuthAccount Account account,
            @PathVariable("turipId") Long favoriteFolderId) {
        FavoriteFolderDetailResponse response = favoriteFolderService.findById(favoriteFolderId, account);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 참여자 목록 조회 api",
            description = "함께 튜립의 참여들의 정보를 조회한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderMembersResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 참여자 목록 조회 성공",
                                    value = """
                                            {
                                                "members" : [
                                                    {
                                                        "nickname" : "하루"
                                                    },
                                                    {
                                                        "nickname" : "메이"
                                                    }
                                                ]
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
                                    name = "forbidden",
                                    summary = "요청한 account가 해당 튜립에 참여중이지 않은 경우",
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
                            examples = @ExampleObject(
                                    name = "turip_not_found",
                                    summary = "id에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
    })
    @GetMapping("/{turipId}/members")
    public ResponseEntity<FavoriteFolderMembersResponse> readMembersById(
            @Parameter(hidden = true) @AuthAccount Account account, @PathVariable("turipId") Long favoriteFolderId) {
        FavoriteFolderMembersResponse response = favoriteFolderService.findMembersById(favoriteFolderId, account);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 이름 수정 api",
            description = "튜립 이름을 수정한다."
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
                                    summary = "튜립 이름 수정 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "accountId": 1,
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
                                            name = "turip_name_blank",
                                            summary = "튜립 이름이 공백인 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_BLANK",
                                                        "message": "찜폴더 이름은 비워둘 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "turip_name_length_over",
                                            summary = "튜립 이름이 20글자를 초과하는 경우",
                                            value = """
                                                    {
                                                        "tag": "FAVORITE_FOLDER_NAME_TOO_LONG",
                                                        "message": "찜폴더 이름의 최대 길이를 초과했습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "is_default_turip",
                                            summary = "튜립이 기본 튜립인 경우",
                                            value = """
                                                    {
                                                        "tag": "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                        "message": "기본 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                            summary = "튜립 소유 계정과 요청 계정이 같지 않은 경우",
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
                            examples = @ExampleObject(
                                    name = "turip_not_found",
                                    summary = "id에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "turip_name_already_exists",
                                    summary = "중복되는 튜립 이름이 존재하는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NAME_CONFLICT",
                                                "message": "이미 존재하는 찜폴더 이름입니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/{turipId}")
    public ResponseEntity<FavoriteFolderResponse> updateName(
            @Parameter(hidden = true) @AuthAccount Account account,
            @PathVariable("turipId") Long favoriteFolderId,
            @RequestBody FavoriteFolderNameRequest request
    ) {
        FavoriteFolderResponse response = favoriteFolderService.updateName(account, favoriteFolderId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "튜립 삭제 api",
            description = "튜립을 삭제한다."
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
                            examples = {
                                    @ExampleObject(
                                            name = "is_default_turip",
                                            summary = "삭제하려는 튜립이 기본 튜립인 경우",
                                            value = """
                                                    {
                                                        "tag": "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                        "message": "기본 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "is_shared_turip",
                                            summary = "삭제하려는 튜립이 함께 튜립인 경우",
                                            value = """
                                                    {
                                                        "tag": "SHARED_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                        "message": "공유 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                    summary = "튜립 소유자의 기기id와 요청자의 기기id가 같지 않은 경우",
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
                            examples = @ExampleObject(
                                    name = "turip_not_found",
                                    summary = "id에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/{turipId}")
    public ResponseEntity<Void> delete(
            @Parameter(hidden = true) @AuthAccount Account account,
            @PathVariable("turipId") Long favoriteFolderId) {
        favoriteFolderService.remove(account, favoriteFolderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "튜립 나가기 api",
            description = "함께 튜립에서 나간다. 함께 튜립의 마지막 참여자인 경우 튜립을 삭제한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FavoriteFolderExitResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "튜립 나가기 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "turipId": 1,
                                                "isDeleted": true
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
                                            name = "personal folder not valid",
                                            summary = "개인 찜폴더인 경우",
                                            value = """
                                                    {
                                                    	"tag": "PERSONAL_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                    	"message": "개인 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "default folder not valid",
                                            summary = "기본 찜폴더인 경우",
                                            value = """
                                                    {
                                                    	"tag": "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                    	"message": "기본 찜폴더에는 이 작업을 수행할 수 없습니다."
                                                    }
                                                    """
                                    )
                            }
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
                                    name = "guest_forbidden",
                                    summary = "게스트가 해당 기능에 접근하려는 경우",
                                    value = """
                                            {
                                                "tag": "GUEST_FORBIDDEN",
                                                "message": "게스트 계정이 접근할 수 없는 기능입니다."
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
                                    name = "turip_not_found",
                                    summary = "turipId에 대한 튜립을 찾을 수 없는 경우",
                                    value = """
                                            {
                                                "tag": "FAVORITE_FOLDER_NOT_FOUND",
                                                "message": "찜폴더를 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @DeleteMapping("/{turipId}/exit")
    public ResponseEntity<FavoriteFolderExitResponse> exit(@Parameter(hidden = true) @AuthMember Member member,
                                                           @PathVariable("turipId") Long favoriteFolderId) {
        FavoriteFolderExitResponse response = favoriteFolderService.exitFolder(member.getAccount(), favoriteFolderId);
        return ResponseEntity.ok(response);
    }
}
