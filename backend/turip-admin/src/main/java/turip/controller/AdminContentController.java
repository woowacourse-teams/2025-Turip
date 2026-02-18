package turip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Member;
import turip.auth.resolver.AuthMember;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.service.AdminContentService;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @PostMapping
    public ResponseEntity<Long> save(@AuthMember Member member, @RequestBody AdminContentSaveRequest request) {
        Long contentId = adminContentService.save(request);
        log.info("[콘텐츠 수집] nickname: {}", member.getAccount().getNickname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contentId);
    }
}
