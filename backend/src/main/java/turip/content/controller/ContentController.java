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
import turip.content.controller.dto.response.ContentsByCityResponse;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.service.ContentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contents")
public class ContentController {

    private final ContentService contentService;

    @GetMapping(value = "/count", params = "cityName")
    public ResponseEntity<ContentCountResponse> readCountByCityName(
            @RequestParam(name = "cityName") String cityName) {
        ContentCountResponse response = contentService.countByCityName(cityName);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "cityName")
    public ResponseEntity<ContentsByCityResponse> readContentsByRegionName(
            @RequestParam(name = "cityName") String cityName,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentsByCityResponse response = contentService.findContentsByCityName(cityName, pageSize,
                lastContentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "/count", params = "keyword")
    public ResponseEntity<ContentCountResponse> readCountByKeyword(
            @RequestParam(name = "keyword") String keyword) {
        ContentCountResponse response = contentService.countByKeyword(keyword);
        return ResponseEntity.ok(response);
    }

    @GetMapping(params = "keyword")
    public ResponseEntity<ContentSearchResponse> readContentsByKeyword(
            @RequestParam(name = "keyword") String keyword,
            @RequestParam(name = "size") Integer pageSize,
            @RequestParam(name = "lastId") Long lastContentId
    ) {
        ContentSearchResponse response = contentService.searchContentsByKeyword(keyword, pageSize, lastContentId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContentResponse> readContent(@PathVariable Long id) {
        ContentResponse response = contentService.getById(id);
        return ResponseEntity.ok(response);
    }
}
