package turip.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.Member;
import turip.account.domain.TuripMember;
import turip.auth.resolver.AuthMember;
import turip.content.domain.ContentPendingData;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;
import turip.controller.dto.response.ContentPendingRejectResponse;
import turip.controller.dto.response.ContentPendingResponse;
import turip.controller.dto.response.PendingListResponse;
import turip.resolver.AuthAdmin;
import turip.service.AdminContentPendingService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/content-pendings")
public class AdminPendingContentController {

    private final AdminContentPendingService adminContentPendingService;

    @GetMapping("/list")
    public ResponseEntity<List<PendingListResponse>> findAll(@AuthAdmin TuripMember admin) {
        return ResponseEntity.ok(adminContentPendingService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentPendingResponse> findById(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id
    ) {
        ContentPendingResponse response = adminContentPendingService.findById(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Long> pending(
            @AuthMember Member member,
            @RequestBody ContentPendingData contentData
    ) {
        Long pendingId = adminContentPendingService.pending(member.getAccount(), contentData);
        return ResponseEntity.status(HttpStatus.CREATED).body(pendingId);
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ContentPendingApprovalResponse> approve(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id
    ) {
        ContentPendingApprovalResponse response = adminContentPendingService.approve(id,
                admin.getMember().getAccount());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ContentPendingRejectResponse> reject(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id,
            @RequestBody ContentPendingRejectRequest request
    ) {
        ContentPendingRejectResponse response = adminContentPendingService.reject(id, admin.getMember().getAccount(),
                request);
        return ResponseEntity.ok(response);
    }
}
