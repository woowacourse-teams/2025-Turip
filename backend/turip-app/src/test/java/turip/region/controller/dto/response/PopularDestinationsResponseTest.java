package turip.region.controller.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.region.controller.dto.response.PopularDestinationsResponse.PopularDestination;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.RegionPopularitySnapshot;

class PopularDestinationsResponseTest {

    @DisplayName("방문 인원수 상위 10개만 순위와 함께 내림차순으로 내려준다")
    @Test
    void ranksTop10Descending() {
        // given - 11개 카테고리 중 가장 낮은 공주(50)는 Top10에서 제외되어야 한다
        Map<DomesticRegionCategory, Long> counts = new EnumMap<>(DomesticRegionCategory.class);
        counts.put(DomesticRegionCategory.SEOUL, 1000L);
        counts.put(DomesticRegionCategory.BUSAN, 900L);
        counts.put(DomesticRegionCategory.JEJU, 800L);
        counts.put(DomesticRegionCategory.INCHEON, 700L);
        counts.put(DomesticRegionCategory.DAEGU, 600L);
        counts.put(DomesticRegionCategory.DAEJEON, 500L);
        counts.put(DomesticRegionCategory.GYEONGJU, 400L);
        counts.put(DomesticRegionCategory.GANGNEUNG, 300L);
        counts.put(DomesticRegionCategory.JEONJU, 200L);
        counts.put(DomesticRegionCategory.SUWON, 100L);
        counts.put(DomesticRegionCategory.GONGJU, 50L);
        RegionPopularitySnapshot snapshot = new RegionPopularitySnapshot("202506", List.of(), counts);

        // when
        PopularDestinationsResponse response = PopularDestinationsResponse.from(snapshot);

        // then
        List<PopularDestination> destinations = response.destinations();
        assertAll(
                () -> assertThat(destinations).hasSize(10),
                () -> assertThat(destinations.get(0).rank()).isEqualTo(1),
                () -> assertThat(destinations.get(0).regionCategory()).isEqualTo("서울"),
                () -> assertThat(destinations.get(0).visitorCount()).isEqualTo(1000L),
                () -> assertThat(destinations.get(9).rank()).isEqualTo(10),
                () -> assertThat(destinations.get(9).regionCategory()).isEqualTo("수원"),
                () -> assertThat(destinations).extracting(PopularDestination::regionCategory)
                        .doesNotContain("공주")  // 11위는 제외
        );
    }
}
