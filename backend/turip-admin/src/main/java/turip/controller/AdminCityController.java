package turip.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.controller.dto.response.AdminCityResponse;
import turip.region.service.CityService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/cities")
public class AdminCityController {

    private final CityService cityService;

    @GetMapping
    public ResponseEntity<List<AdminCityResponse>> getAllCities() {
        List<AdminCityResponse> cities = cityService.findAll().stream()
                .map(AdminCityResponse::from)
                .toList();
        return ResponseEntity.ok(cities);
    }
}
