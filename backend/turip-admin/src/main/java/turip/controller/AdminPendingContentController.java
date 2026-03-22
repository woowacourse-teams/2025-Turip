package turip.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.TuripMember;
import turip.content.domain.ContentPending;
import turip.controller.dto.response.ContentPendingResponse;
import turip.resolver.AuthAdmin;
import turip.service.AdminContentPendingService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/pending-contents")
public class AdminPendingContentController {

    private final AdminContentPendingService adminContentPendingService;

    @GetMapping("/{id}")
    public ResponseEntity<ContentPendingResponse> findById(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id
    ) {
        ContentPending contentPending = adminContentPendingService.findById(id);
        ContentPendingResponse response = ContentPendingResponse.from(contentPending);
        return ResponseEntity.ok(response);
    }
}
