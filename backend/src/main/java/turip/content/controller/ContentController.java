package turip.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentResponse;
import turip.content.controller.dto.response.ContentsByRegionResponse;
import turip.content.service.ContentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/count")
    public ResponseEntity<ContentCountResponse> readCountByRegionName(
            @RequestParam(name = "region") String regionName) {
        ContentCountResponse response = contentService.countByRegionName(regionName);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<ContentsByRegionResponse> readContentsByRegionName(
            @RequestParam(name = "region") String regionName,
            @RequestParam(name = "pageSize") Integer pageSize,
            @RequestParam(name = "lastContentId") Long lastContentId
    ) {
        ContentsByRegionResponse response = contentService.findContentsByRegionName(regionName, pageSize,
                lastContentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> readContent(@PathVariable Long id) {
        ContentResponse response = contentService.getById(id);
        return ResponseEntity.ok(response);
    }
}
