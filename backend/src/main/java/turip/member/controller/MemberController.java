package turip.member.controller;

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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.auth.resolver.AuthGuest;
import turip.auth.resolver.AuthMember;
import turip.common.exception.ErrorResponse;
import turip.member.domain.Guest;
import turip.member.domain.Member;
import turip.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    @Operation(
            summary = "마이그레이션 api",
            description = "게스트의 데이터를 멤버의 데이터로 마이그레이션한다."
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
    @PostMapping("/migration")
    public ResponseEntity<Void> migrate(@Parameter(hidden = true) @AuthMember Member member,
                                        @Parameter(hidden = true) @AuthGuest Guest guest) {
        memberService.migrate(member, guest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> delete(@Parameter(hidden = true) @AuthMember Member member) {
        memberService.delete(member);
        return ResponseEntity.noContent().build();
    }
}
