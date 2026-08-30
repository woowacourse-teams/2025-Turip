package turip.region.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import turip.region.controller.dto.response.PopularDestinationsResponse;
import turip.region.controller.dto.response.RegionPopularityResponse;
import turip.region.domain.RegionPopularitySnapshot;
import turip.region.service.RegionPopularityService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Tag(name = "RegionPopularity", description = "지역별 관광 인기도 API")
public class RegionPopularityController {

    private final RegionPopularityService regionPopularityService;

    @Operation(
            summary = "지역별 관광 인기도 조회",
            description = "최근 한 달 기준 전체 시도의 방문 인원수(외지인 + 외국인)를 조회한다. 지원하지 않는 시도도 포함하며, 방문 인원수 내림차순으로 정렬된다."
    )
    @GetMapping("/regions/popularity")
    public ResponseEntity<RegionPopularityResponse> readRegionPopularity() {
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();
        return ResponseEntity.ok(RegionPopularityResponse.from(snapshot));
    }

    @Operation(
            summary = "최근 한 달 인기 여행지 Top 10 조회",
            description = "튜립이 지원하는 지역 카테고리 중 최근 한 달 방문 인원수(외지인 + 외국인) 상위 10개를 순위와 함께 조회한다. "
    )
    @GetMapping("/regions/popular-destinations")
    public ResponseEntity<PopularDestinationsResponse> readPopularDestinations() {
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();
        return ResponseEntity.ok(PopularDestinationsResponse.from(snapshot));
    }
}
