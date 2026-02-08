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
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.auth.resolver.AuthAccount;
import turip.auth.resolver.AuthMember;
import turip.common.exception.ErrorResponse;
import turip.favorite.controller.dto.request.FavoriteFolderNameRequest;
import turip.favorite.controller.dto.request.FavoriteFolderRequest;
import turip.favorite.controller.dto.response.FavoriteFolderResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithFavoriteStatusResponse;
import turip.favorite.controller.dto.response.FavoriteFoldersWithPlaceCountResponse;
import turip.favorite.service.FavoriteFolderService;
import turip.favorite.service.FavoriteFolderStreamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/turips")
@Tag(name = "Turip", description = "튜립 API")
public class FavoriteFolderController {

    private final FavoriteFolderService favoriteFolderService;
    private final FavoriteFolderStreamService favoriteFolderStreamService;

    @Operation(
            summary = "튜립 실시간 업데이트 스트림 연결 api",
            description = """
                    튜립의 실시간 업데이트를 받기 위한 SSE(Server-Sent Events) 스트림에 연결한다.
                    ※ Swagger UI에서는 SSE 스트림의 실시간 확인이 제한적입니다. 실제 테스트는 postman 등을 사용하세요.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "SSE 스트림 연결 성공 및 이벤트 예시",
                    content = @Content(
                            mediaType = "text/event-stream",
                            examples = {
                                    @ExampleObject(
                                            name = "connect_event",
                                            summary = "연결 성공 이벤트",
                                            value = """
                                                    event: connect
                                                    id: 1738987654321
                                                    data: {"folderId":1,"timestamp":"2025-02-08T12:00:00"}
                                                    
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "folder_update_place_added",
                                            summary = "장소 추가 이벤트",
                                            value = """
                                                    event: folder-update
                                                    id: 1738987654322
                                                    data: {"folderId":1,"action":"PLACE_ADDED","timestamp":"2025-02-08T12:01:00"}
                                                    
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "heartbeat_event",
                                            summary = "하트비트 이벤트",
                                            value = """
                                                    event: heartbeat
                                                    id: 1738987654326
                                                    data: {"timestamp":"2025-02-08T12:05:00"}
                                                    
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
                                    name = "not_turip_member",
                                    summary = "해당 튜립에 접근 권한이 없는 경우",
                                    value = """
                                            {
                                                "tag": "FOLDER_STREAM_FORBIDDEN",
                                                "message": "폴더 스트림에 접근할 권한이 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping(value = "/{turipId}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(
            @PathVariable("turipId") Long favoriteFolderId,
            @Parameter(hidden = true) @AuthMember Member member) {
        return favoriteFolderStreamService.createEmitter(favoriteFolderId, member);
    }

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
            summary = "튜립 조회 api",
            description = "특정 회원의 튜립 목록을 조회한다."
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
                                    summary = "튜립 조회 성공",
                                    value = """
                                            {
                                                "turips": [
                                                    {
                                                        "id": 5,
                                                        "accountId": 8,
                                                        "name": "기본 폴더",
                                                        "isDefault": true,
                                                        "placeCount": 0
                                                    },
                                                    {
                                                        "id": 6,
                                                        "accountId": 8,
                                                        "name": "뭉치가 가고싶은 맛집들",
                                                        "isDefault": false,
                                                        "placeCount": 0
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
    public ResponseEntity<FavoriteFoldersWithPlaceCountResponse> readAllByMember(
            @Parameter(hidden = true) @AuthAccount Account account) {
        FavoriteFoldersWithPlaceCountResponse response = favoriteFolderService.findAllByMember(account);
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
                            examples = @ExampleObject(
                                    name = "is_default_turip",
                                    summary = "삭제하려는 튜립이 기본 튜립인 경우",
                                    value = """
                                            {
                                                "tag": "DEFAULT_FAVORITE_FOLDER_OPERATION_NOT_ALLOWED",
                                                "message": "기본 찜폴더에는 이 작업을 수행할 수 없습니다."
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
}
