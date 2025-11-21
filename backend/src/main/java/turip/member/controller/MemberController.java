package turip.member.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import turip.auth.resolver.AuthGuest;
import turip.auth.resolver.AuthMember;
import turip.member.domain.Guest;
import turip.member.domain.Member;
import turip.member.service.MemberService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
@Tag(name = "Member", description = "회원 API")
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/migration")
    public ResponseEntity<Void> migrate(@Parameter(hidden = true) @AuthMember Member member,
                                        @Parameter(hidden = true) @AuthGuest Guest guest) {
        memberService.migrate(member, guest);
        return ResponseEntity.noContent().build();
    }
}
