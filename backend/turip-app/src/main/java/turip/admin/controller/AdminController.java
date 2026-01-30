package turip.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import turip.admin.service.AdminService;
import turip.auth.controller.dto.request.TuripLoginRequest;
import turip.auth.controller.dto.response.TokenResult;
import turip.auth.util.TokenCookieUtil;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@Tag(name = "Admin", description = "어드민 관련 API")
public class AdminController {

    private final AdminService adminService;
    private final TokenCookieUtil tokenCookieUtil;

    @Operation(
            summary = "어드민 로그인 api",
            description = "튜립 어드민 로그인을 진행한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = """
                            로그인 성공
                            
                            응답 헤더에 두 개의 Set-Cookie가 포함됩니다:
                            - accessToken: 인증 토큰 (30분)
                            - refreshToken: 갱신 토큰 (7일)
                            
                            모든 쿠키는 HttpOnly, Secure, SameSite=Strict 속성을 가집니다.
                            """,
                    headers = @Header(
                            name = "Set-Cookie",
                            description = "인증 쿠키",
                            schema = @Schema(
                                    type = "string",
                                    example = """
                                            accessToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; Max-Age=1800; HttpOnly; Secure; SameSite=Strict
                                            refreshToken=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...; Path=/; Max-Age=604800; HttpOnly; Secure; SameSite=Strict
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
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "실패 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(
                                    name = "invalid Role",
                                    summary = "어드민이 아닌 경우",
                                    value = """
                                            {
                                            	"tag": "FORBIDDEN",
                                            	"message": "접근 권한이 없습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping("/login/admin")
    public ResponseEntity<Void> login(
            @Parameter(hidden = true) @RequestHeader("device-fid") String deviceFid,
            @RequestBody TuripLoginRequest request) {
        TokenResult result = adminService.login(request, deviceFid);

        ResponseCookie accessTokenCookie = tokenCookieUtil.createAccessTokenCookie(result.accessToken());
        ResponseCookie refreshTokenCookie = tokenCookieUtil.createRefreshTokenCookie(result.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .build();
    }
}
