package turip.creator.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.creator.controller.dto.response.CreatorResponse;
import turip.creator.service.CreatorService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/creators")
public class CreatorController {

    private final CreatorService creatorService;

    @GetMapping("/{id}")
    public ResponseEntity<CreatorResponse> readCreatorById(@PathVariable Long id) {
        CreatorResponse response = creatorService.getById(id);
        return ResponseEntity.ok(response);
    }
}
