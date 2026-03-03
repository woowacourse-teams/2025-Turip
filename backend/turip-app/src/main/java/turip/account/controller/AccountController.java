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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.controller.dto.response.AccountResponse;
import turip.account.domain.Account;
import turip.auth.resolver.AuthAccount;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account", description = "계정 API")
public class AccountController {

    @Operation(
            summary = "내 계정 정보 조회 api",
            description = "access token이 유효한지 확인하고 계정 정보를 반환한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "nickname": "여행하는 튜립",
                                                "role": "MEMBER"
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
    @GetMapping("/me")
    public ResponseEntity<AccountResponse> read(@Parameter(hidden = true) @AuthAccount Account account) {
        AccountResponse response = AccountResponse.from(account);
        return ResponseEntity.ok(response);
    }
}
