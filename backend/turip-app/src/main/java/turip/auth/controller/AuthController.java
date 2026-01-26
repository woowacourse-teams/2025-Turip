package turip.auth.controller;

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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import turip.account.domain.Account;
import turip.account.domain.Member;
import turip.account.domain.Provider;
import turip.auth.controller.dto.request.GoogleLoginRequest;
import turip.auth.controller.dto.request.RefreshTokenRequest;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.LoginResponse;
import turip.auth.controller.dto.response.RefreshTokenResponse;
import turip.auth.resolver.AuthAccount;
import turip.auth.resolver.AuthMember;
import turip.auth.service.AuthService;
import turip.common.exception.ErrorResponse;

@Controller
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "자체 로그인 api",
            description = "튜립 자체 로그인을 진행한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "로그인 성공",
                                    value = """
                                            {
                                              "accessToken": "jwt-access",
                                              "refreshToken": "jwt-refresh",
                                              "isNewMember": false
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
                            examples = @ExampleObject(
                                    name = "invalid credential",
                                    summary = "올바르지 않은 자격 증명",
                                    value = """
                                            {
                                            	"tag": "CREDENTIALS_INVALID",
                                            	"message": "아이디 또는 비밀번호가 올바르지 않습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login/turip")
    public ResponseEntity<LoginResponse> loginWithTurip(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @RequestBody TuripLoginRequest request) {
        LoginResponse response = authService.loginWithTurip(request, deviceFid);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "구글 로그인 api",
            description = "id token 기반 구글 소셜 로그인을 진행한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "로그인 성공",
                                    value = """
                                            {
                                              "accessToken": "jwt-access",
                                              "refreshToken": "jwt-refresh",
                                              "isNewMember": false
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
                                            name = "invalid id token",
                                            summary = "올바르지 않은 id token",
                                            value = """
                                                    {
                                                    	"tag": "ID_TOKEN_NOT_VALID",
                                                    	"message": "유효하지 않은 id token입니다."
                                                    }
                                                    """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/login/google")
    public ResponseEntity<LoginResponse> loginWithGoogle(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @RequestBody GoogleLoginRequest request) {
        LoginResponse response = authService.loginWithSocial(request, Provider.GOOGLE, deviceFid);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "JWT access token 재발급 api",
            description = "refresh token을 통해 access token을 재발급받는다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RefreshTokenResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "재발급 성공",
                                    value = """
                                            {
                                              "accessToken": "new-jwt-access",
                                              "refreshToken": "new-jwt-refresh"
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
                                            name = "invalid refresh token",
                                            summary = "유효하지 않은 refresh token",
                                            value = """
                                                    {
                                                      "tag": "REFRESH_TOKEN_INVALID",
                                                      "message": "유효하지 않은 refresh token입니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid signature refresh token",
                                            summary = "서명값이 올바르지 않은 refresh token",
                                            value = """
                                                    {
                                                      "tag" : "REFRESH_TOKEN_SIGNATURE_INVALID",
                                                      "message": "refresh token이 위조됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "expired refresh token",
                                            summary = "만료된 refresh token",
                                            value = """
                                                    {
                                                      "tag" : "REFRESH_TOKEN_EXPIRED",
                                                      "message": "refresh token이 만료됐습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "refresh token not found",
                                            summary = "refresh token을 찾을 수 없는 경우",
                                            value = """
                                                    {
                                                      "tag" : "REFRESH_TOKEN_NOT_FOUND",
                                                      "message": "refresh token을 찾을 수 없습니다."
                                                    }
                                                    """
                                    ),
                            }
                    )
            )
    })
    @PostMapping("/token")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @RequestBody RefreshTokenRequest request) {
        RefreshTokenResponse response = authService.refresh(request, deviceFid);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "로그아웃 api",
            description = "해당 계정, 기기에 대한 refresh token을 삭제한다."
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
                                    name = "device-fid required",
                                    summary = "요청 헤더에 device fid가 존재하지 않는 경우",
                                    value = """
                                            {
                                            	"tag": "DEVICE_FID_REQUIRED",
                                            	"message": "요청 헤더에 device_fid가 존재하지 않습니다."
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
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
                                       @Parameter(hidden = true) @AuthAccount Account account) {
        authService.logout(account, deviceFid);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "access token 유효성 검사 api",
            description = "access token이 유효한지 확인한다."
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
            )
    })
    @GetMapping("/token/verification")
    public ResponseEntity<Void> verify(@Parameter(hidden = true) @AuthMember Member member) {
        return ResponseEntity.noContent().build();
    }
}
