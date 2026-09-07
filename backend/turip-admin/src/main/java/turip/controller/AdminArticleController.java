package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import turip.account.domain.TuripMember;
import turip.controller.dto.request.AdminArticleCreateRequest;
import turip.controller.dto.request.AdminArticleOrderRequest;
import turip.controller.dto.request.AdminArticleUpdateRequest;
import turip.controller.dto.response.AdminArticleImageResponse;
import turip.controller.dto.response.AdminArticleResponse;
import turip.controller.dto.response.AdminArticlesResponse;
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

    @GetMapping
    public ResponseEntity<AdminArticlesResponse> findArticles(
            @AuthAdmin TuripMember admin,
            @RequestParam(required = false, defaultValue = "10") Integer size,
            @RequestParam(required = false) Long lastId
    ) {
        return ResponseEntity.ok(adminArticleService.findArticles(size, lastId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminArticleResponse> getArticle(@AuthAdmin TuripMember admin, @PathVariable Long id) {
        return ResponseEntity.ok(adminArticleService.getArticle(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<AdminArticleResponse> update(
            @AuthAdmin TuripMember admin,
            @PathVariable Long id,
            @RequestBody AdminArticleUpdateRequest request
    ) {
        return ResponseEntity.ok(adminArticleService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@AuthAdmin TuripMember admin, @PathVariable Long id) {
        adminArticleService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/images")
    public ResponseEntity<AdminArticleImageResponse> uploadImage(
            @AuthAdmin TuripMember admin,
            @RequestParam("image") MultipartFile image
    ) {
        String url = adminArticleService.uploadImage(image);
        return ResponseEntity.status(HttpStatus.CREATED).body(AdminArticleImageResponse.from(url));
    }

    @PatchMapping("/order")
    public ResponseEntity<Void> reorder(
            @AuthAdmin TuripMember admin,
            @RequestBody AdminArticleOrderRequest request
    ) {
        adminArticleService.reorder(request);
        return ResponseEntity.ok().build();
    }
}
