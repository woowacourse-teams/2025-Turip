package turip.tripcourse.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import turip.tripcourse.controller.dto.response.TripCourseDetailResponse;
import turip.tripcourse.service.TripCourseService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trip-courses")
public class TripCourseController {

    private final TripCourseService tripCourseService;

    @GetMapping
    public ResponseEntity<TripCourseDetailResponse> readTripCourseDetails(
            @RequestParam(name = "contentId") Long contentId) {
        TripCourseDetailResponse response = tripCourseService.findTripCourseDetails(contentId);
        return ResponseEntity.ok(response);
    }
}
