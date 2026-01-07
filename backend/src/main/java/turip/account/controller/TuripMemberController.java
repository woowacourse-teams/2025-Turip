package turip.account.controller;

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

@RestController
@RequiredArgsConstructor
@RequestMapping("/turip-members")
@Tag(name = "TuripMember", description = "튜립 자체 회원 API")
public class TuripMemberController {

    private final TuripMemberService turipMemberService;

    @PostMapping
    public ResponseEntity<TuripMemberResponse> create(@RequestBody TuripMemberRequest request) {
        TuripMemberResponse response = turipMemberService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
