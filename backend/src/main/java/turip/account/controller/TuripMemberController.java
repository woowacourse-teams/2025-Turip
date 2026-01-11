package turip.account.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.controller.dto.request.TuripMemberRequest;
import turip.account.controller.dto.response.TuripMemberResponse;
import turip.account.service.TuripMemberService;
import turip.common.exception.ErrorResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/turip-members")
@Tag(name = "TuripMember", description = "튜립 자체 회원 API")
public class TuripMemberController {

    private final TuripMemberService turipMemberService;

    @Operation(
            summary = "자체 회원가입 api",
            description = "튜립 자체 회원가입을 진행한다."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "성공 예시",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TuripMemberResponse.class),
                            examples = @ExampleObject(
                                    name = "success",
                                    summary = "로그인 성공",
                                    value = """
                                            {
                                                "id": 1,
                                                "memberId": 15,
                                                "loginId": "turip"
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
                                            name = "invalid login_password",
                                            summary = "올바르지 않은 비밀번호 형식",
                                            value = """
                                                    {
                                                    	"tag": "LOGIN_PASSWORD_INVALID",
                                                    	"message": "비밀번호 형식이 올바르지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid login_id",
                                            summary = "올바르지 않은 아이디 형식",
                                            value = """
                                                    {
                                                    	"tag": "LOGIN_ID_INVALID",
                                                    	"message": "아이디 형식이 올바르지 않습니다."
                                                    }
                                                    """
                                    ),
                                    @ExampleObject(
                                            name = "invalid email",
                                            summary = "올바르지 않은 이메일 형식",
                                            value = """
                                                    {
                                                    	"tag": "EMAIL_INVALID",
                                                    	"message": "이메일 형식이 올바르지 않습니다."
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
                            examples = @ExampleObject(
                                    name = "login_id conflict",
                                    summary = "아이디 중복",
                                    value = """
                                            {
                                            	"tag": "LOGIN_ID_CONFLICT",
                                            	"message": "중복 아이디가 존재합니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<TuripMemberResponse> create(@RequestBody TuripMemberRequest request) {
        TuripMemberResponse response = turipMemberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
