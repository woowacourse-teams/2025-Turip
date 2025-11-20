package turip.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import turip.auth.controller.dto.request.LoginRequest;
import turip.auth.controller.dto.response.LoginResponse;
import turip.auth.service.AuthService;
import turip.common.exception.ErrorResponse;
import turip.member.domain.Provider;

@Controller
@RequiredArgsConstructor
@Tag(name = "Auth", description = "인증 API")
public class AuthController {

    private final AuthService authService;

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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request, Provider.GOOGLE);
        return ResponseEntity.ok(response);
    }
}
