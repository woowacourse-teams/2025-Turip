package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.account.domain.TuripMember;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.resolver.AuthAdmin;
import turip.service.AdminArticleService;

@RestController
@RequestMapping("/api/v1/admin/articles")
@RequiredArgsConstructor
public class AdminArticleController {

    private final AdminArticleService adminArticleService;

    @PostMapping
    public ResponseEntity<Long> create(@AuthAdmin TuripMember admin, @RequestBody AdminArticleCreateRequest request) {
        Long articleId = adminArticleService.create(request, admin);
        return ResponseEntity.status(HttpStatus.CREATED).body(articleId);
    }
}
