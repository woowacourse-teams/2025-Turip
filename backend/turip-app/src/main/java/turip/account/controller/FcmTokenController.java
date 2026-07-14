package turip.account.controller;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.controller.dto.request.FcmTokenNotificationRequest;
import turip.account.controller.dto.request.FcmTokenRegisterRequest;
import turip.account.domain.Account;
import turip.account.service.FcmTokenService;
import turip.auth.resolver.AuthAccount;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm-tokens")
@Tag(name = "FcmToken", description = "FCM 토큰 API")
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @Operation(
            summary = "FCM 토큰 등록/갱신 api",
            description = "현재 로그인한 회원의 해당 기기(device-fid)에 대한 FCM 토큰을 등록하거나 갱신한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "등록/갱신 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "fcm token blank",
                                            summary = "FCM 토큰이 빈 값인 경우",
                                            value = """
                                                    {
                                                        "tag": "FCM_TOKEN_BLANK",
                                                        "message": "FCM 토큰은 비워둘 수 없습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "device fid required",
                                            summary = "device-fid 헤더가 빈 값인 경우",
                                            value = """
                                                    {
                                                        "tag": "DEVICE_FID_REQUIRED",
                                                        "message": "요청 헤더에 device_fid가 존재하지 않습니다."
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
            )
    })
    @PostMapping
    public ResponseEntity<Void> register(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestBody FcmTokenRegisterRequest request) {
        fcmTokenService.registerOrUpdate(account, deviceFid, request.token());
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "FCM 알림 수신 여부 변경 api",
            description = "현재 로그인한 회원의 해당 기기(device-fid)에 대한 알림 수신 여부를 변경한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "변경 성공"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "notification enabled required",
                                    summary = "알림 수신 여부 값이 누락된 경우",
                                    value = """
                                            {
                                                "tag": "NOTIFICATION_ENABLED_REQUIRED",
                                                "message": "알림 수신 여부는 필수 값입니다."
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
                                    name = "fcm token not found",
                                    summary = "해당 기기의 FCM 토큰이 존재하지 않는 경우",
                                    value = """
                                            {
                                                "tag": "FCM_TOKEN_NOT_FOUND",
                                                "message": "FCM 토큰을 찾을 수 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PatchMapping("/notification")
    public ResponseEntity<Void> changeNotificationEnabled(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @Parameter(hidden = true) @AuthAccount Account account,
            @RequestBody FcmTokenNotificationRequest request) {
        fcmTokenService.changeNotificationEnabled(account, deviceFid, request.notificationEnabled());
        return ResponseEntity.noContent().build();
    }
}
