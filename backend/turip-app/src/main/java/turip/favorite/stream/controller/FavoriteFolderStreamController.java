package turip.favorite.stream.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import turip.account.domain.Member;
import turip.auth.resolver.AuthMember;
import turip.common.exception.ErrorResponse;
import turip.favorite.stream.service.FavoriteFolderStreamService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/turips")
@Tag(name = "Turip Stream", description = "튜립 실시간 스트리밍 API")
public class FavoriteFolderStreamController {

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
}
