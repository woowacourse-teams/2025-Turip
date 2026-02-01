package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.controller.dto.response.AdminVideoResponse;
import turip.service.AdminVideoService;

@RestController
@RequestMapping("/api/v1/admin/video")
@RequiredArgsConstructor
public class AdminVideoController {

    private final AdminVideoService adminVideoService;

    @GetMapping
    public ResponseEntity<AdminVideoResponse> getVideoInfo(@RequestParam String url) {
        AdminVideoResponse response = adminVideoService.getVideoData(url);
        return ResponseEntity.ok(response);
    }
}
