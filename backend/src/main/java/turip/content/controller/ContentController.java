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
import turip.content.controller.dto.response.ContentsByRegionCategoryResponse;
import turip.content.service.ContentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping("/count")
    public ResponseEntity<ContentCountResponse> readCountByCityName(
            @RequestParam(name = "cityName") String cityName) {
        ContentCountResponse response = contentService.countByCityName(cityName);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    public ResponseEntity<ContentsByRegionCategoryResponse> readContentsByRegionCategory(
            @RequestParam(name = "regionCategory") String regionCategory,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentsByRegionCategoryResponse response
                = contentService.getContentsByRegionCategory(regionCategory, pageSize, lastContentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> readContent(@PathVariable Long id) {
        ContentResponse response = contentService.getById(id);
        return ResponseEntity.ok(response);
    }
}
