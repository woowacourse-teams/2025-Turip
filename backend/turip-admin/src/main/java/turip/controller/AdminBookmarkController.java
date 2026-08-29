package turip.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.TuripMember;
import turip.controller.dto.response.AdminBookmarkStatusResponse;
import turip.resolver.AuthAdmin;
import turip.service.AdminBookmarkService;

@RestController
@RequestMapping("/api/v1/admin/bookmarks")
@RequiredArgsConstructor
public class AdminBookmarkController {

    private final AdminBookmarkService adminBookmarkService;

    @GetMapping
    public ResponseEntity<List<AdminBookmarkStatusResponse>> findAccountBookmarkStatuses(
            @AuthAdmin TuripMember admin,
            @RequestParam(name = "contentId") Long contentId
    ) {
        List<AdminBookmarkStatusResponse> response = adminBookmarkService.findAdminAccountBookmarkStatuses(contentId);
        return ResponseEntity.ok(response);
    }
}
