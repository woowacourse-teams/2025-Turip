package turip.region.controller.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.region.controller.dto.response.RegionPopularityResponse.RegionPopularity;
import turip.region.domain.ProvinceVisitorCount;
import turip.region.domain.RegionPopularitySnapshot;

class RegionPopularityResponseTest {

    @DisplayName("전체 시도를 방문 인원수 내림차순으로 정렬한다 (미지원 시도 포함)")
    @Test
    void sortsAllProvincesDescending() {
        // given - 경기도(41)는 미지원 시도지만 히트맵에는 포함된다
        RegionPopularitySnapshot snapshot = new RegionPopularitySnapshot(
                "202506",
                List.of(
                        new ProvinceVisitorCount(11, "서울특별시", 50L),
                        new ProvinceVisitorCount(41, "경기도", 1000L),
                        new ProvinceVisitorCount(26, "부산광역시", 15L)
                ),
                Map.of()
        );

        // when
        RegionPopularityResponse response = RegionPopularityResponse.from(snapshot);

        // then
        List<RegionPopularity> regions = response.regions();
        assertAll(
                () -> assertThat(regions).extracting(RegionPopularity::regionName)
                        .containsExactly("경기도", "서울특별시", "부산광역시"),  // 내림차순
                () -> assertThat(regions.get(0).areaCode()).isEqualTo(41),
                () -> assertThat(regions.get(0).visitorCount()).isEqualTo(1000L)
        );
    }
}
