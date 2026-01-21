package turip.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.place.controller.dto.response.PlaceSearchResponse;
import turip.place.domain.PlaceSearchType;
import turip.service.AdminPlaceService;

@RestController
@RequestMapping("/api/v1/admin/places")
@RequiredArgsConstructor
public class AdminPlaceController {

    private final AdminPlaceService adminPlaceService;

    @GetMapping("/search")
    public ResponseEntity<PlaceSearchResponse> search(
            @RequestParam String query,
            @RequestParam PlaceSearchType type
    ) {
        PlaceSearchResponse response = adminPlaceService.searchPlaces(query, type);
        return ResponseEntity.ok(response);
    }
}
