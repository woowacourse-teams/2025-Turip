package turip.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.content.controller.dto.response.ContentCountResponse;
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
            @RequestParam(name = "size") Integer size,
            @RequestParam(name = "lastId") Long lastId
    ) {
        ContentsByRegionResponse response = contentService.findContentsByRegionName(regionName, size, lastId);
        return ResponseEntity.ok(response);
    }
}
