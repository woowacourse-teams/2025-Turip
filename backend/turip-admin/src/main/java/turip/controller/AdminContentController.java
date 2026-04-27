package turip.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Member;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.controller.dto.response.MyCollectContentResponse;
import turip.resolver.AuthAdmin;
import turip.service.AdminContentPendingService;
import turip.service.AdminContentService;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;
    private final AdminContentPendingService adminContentPendingService;

    @PostMapping
    public ResponseEntity<Long> save(@AuthAdmin Member member, @RequestBody AdminContentSaveRequest request) {
        Long contentId = adminContentService.save(request);
        log.info("[콘텐츠 수집] nickname: {}", member.getAccount().getNickname());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contentId);
    }

    @GetMapping("/my")
    public ResponseEntity<List<MyCollectContentResponse>> getMyHistory(@AuthAdmin Member member) {
        return ResponseEntity.ok(adminContentPendingService.getMyHistory(member.getAccount()));
    }
}
