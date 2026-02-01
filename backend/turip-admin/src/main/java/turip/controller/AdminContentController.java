package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.controller.dto.request.AdminContentSaveRequest;
import turip.service.AdminContentService;

@RestController
@RequestMapping("/api/v1/admin/contents")
@RequiredArgsConstructor
public class AdminContentController {

    private final AdminContentService adminContentService;

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody AdminContentSaveRequest request) {
        Long contentId = adminContentService.save(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(contentId);
    }
}
