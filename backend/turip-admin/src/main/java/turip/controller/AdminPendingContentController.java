package turip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.TuripMember;
import turip.controller.dto.request.ContentPendingRejectRequest;
import turip.controller.dto.response.ContentPendingApprovalResponse;
import turip.controller.dto.response.ContentPendingRejectResponse;
import turip.controller.dto.response.ContentPendingResponse;
import turip.resolver.AuthAdmin;
import turip.service.AdminContentPendingService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/content-pendings")
public class AdminPendingContentController {

    private final AdminContentPendingService adminContentPendingService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentPendingResponse> findById(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id
    ) {
        ContentPendingResponse response = adminContentPendingService.findById(id);
        return ResponseEntity.ok(response);
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
