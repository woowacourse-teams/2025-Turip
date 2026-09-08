package turip.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.infrastructure.client.KoreaTourismVisitorClient;
import turip.infrastructure.client.dto.KoreaTourismVisitorResponse.VisitorItem;
import turip.infrastructure.client.dto.VisitorFetchResult;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.ProvinceVisitorCount;
import turip.region.domain.RegionPopularitySnapshot;

@ExtendWith(MockitoExtension.class)
class RegionPopularityServiceTest {

    @InjectMocks
    private RegionPopularityService regionPopularityService;

    @Mock
    private KoreaTourismVisitorClient visitorClient;

    @DisplayName("전체 시도를 집계하되 미지원 시도도 포함하고, 외지인·외국인만 합산한다")
    @Test
    void aggregatesAllProvincesIncludingUnsupported() {
        // given
        List<VisitorItem> provinceItems = List.of(
                provinceItem("11", "서울특별시", "1", 100.0),  // 현지인 - 제외
                provinceItem("11", "서울특별시", "2", 30.0),
                provinceItem("11", "서울특별시", "3", 20.0),
                provinceItem("26", "부산광역시", "2", 10.5),
                provinceItem("26", "부산광역시", "3", 4.5),
                provinceItem("41", "경기도", "2", 1000.0)      // 미지원 시도
        );
        given(visitorClient.findLatestBaseMonth()).willReturn(Optional.of("202506"));
        given(visitorClient.fetchProvinceVisitors(any(), any()))
                .willReturn(VisitorFetchResult.success(provinceItems));
        given(visitorClient.fetchCityVisitors(any(), any()))
                .willReturn(VisitorFetchResult.success(List.of()));

        // when
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();

        // then
        assertAll(
                () -> assertThat(snapshot.provinceVisitors()).hasSize(3),
                () -> assertThat(countOfArea(snapshot, 11)).isEqualTo(50L),
                () -> assertThat(countOfArea(snapshot, 26)).isEqualTo(15L),
                () -> assertThat(countOfArea(snapshot, 41)).isEqualTo(1000L),  // 미지원도 포함
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.SEOUL)).isEqualTo(50L),
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.BUSAN)).isEqualTo(15L)
        );
    }

    @DisplayName("시 카테고리는 자신의 시군구 코드로만 필터해 합산한다")
    @Test
    void aggregatesCityBySignguCodesOnly() {
        // given
        List<VisitorItem> cityItems = List.of(
                cityItem("51150", "2", 5.0),   // 강릉시
                cityItem("52111", "2", 5.0),   // 전주 완산구
                cityItem("52113", "2", 5.0),   // 전주 덕진구
                cityItem("41111", "2", 5.0),   // 수원 장안구
                cityItem("41115", "2", 5.0),   // 수원 팔달구
                cityItem("41117", "2", 5.0),   // 수원 영통구
                cityItem("11110", "2", 999.0)  // 종로구 - 어떤 지원 카테고리에도 없음
        );
        given(visitorClient.findLatestBaseMonth()).willReturn(Optional.of("202506"));
        given(visitorClient.fetchProvinceVisitors(any(), any()))
                .willReturn(VisitorFetchResult.success(List.of()));
        given(visitorClient.fetchCityVisitors(any(), any()))
                .willReturn(VisitorFetchResult.success(cityItems));

        // when
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();

        // then
        assertAll(
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.GANGNEUNG)).isEqualTo(5L),
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.JEONJU)).isEqualTo(10L),
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.SUWON)).isEqualTo(15L)
        );
    }

    @DisplayName("광역 조회가 실패하면 시도 히트맵과 시도 카테고리는 제외하고 시 카테고리만 집계한다")
    @Test
    void skipsProvinceWhenProvinceFetchFails() {
        // given
        List<VisitorItem> cityItems = List.of(cityItem("51150", "2", 5.0));
        given(visitorClient.findLatestBaseMonth()).willReturn(Optional.of("202506"));
        given(visitorClient.fetchProvinceVisitors(any(), any())).willReturn(VisitorFetchResult.failure());
        given(visitorClient.fetchCityVisitors(any(), any()))
                .willReturn(VisitorFetchResult.success(cityItems));

        // when
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();

        // then
        assertAll(
                () -> assertThat(snapshot.provinceVisitors()).isEmpty(),
                () -> assertThat(snapshot.categoryCounts()).doesNotContainKey(DomesticRegionCategory.SEOUL),
                () -> assertThat(snapshot.categoryCounts().get(DomesticRegionCategory.GANGNEUNG)).isEqualTo(5L)
        );
    }

    @DisplayName("기준월을 찾지 못하면 스냅샷은 비어 있다")
    @Test
    void keepsEmptySnapshotWhenNoBaseMonth() {
        // given
        given(visitorClient.findLatestBaseMonth()).willReturn(Optional.empty());

        // when
        RegionPopularitySnapshot snapshot = regionPopularityService.getPopularity();

        // then
        assertThat(snapshot.isEmpty()).isTrue();
    }

    private long countOfArea(RegionPopularitySnapshot snapshot, int areaCode) {
        return snapshot.provinceVisitors().stream()
                .filter(province -> province.areaCode() == areaCode)
                .mapToLong(ProvinceVisitorCount::visitorCount)
                .findFirst()
                .orElseThrow();
    }

    private VisitorItem provinceItem(String areaCode, String areaName, String touDivCd, double touNum) {
        VisitorItem item = mock(VisitorItem.class);
        lenient().when(item.getAreaCode()).thenReturn(areaCode);
        lenient().when(item.getAreaName()).thenReturn(areaName);
        lenient().when(item.getTouristDivisionCode()).thenReturn(touDivCd);
        lenient().when(item.getTouristCount()).thenReturn(touNum);
        return item;
    }

    private VisitorItem cityItem(String signguCode, String touDivCd, double touNum) {
        VisitorItem item = mock(VisitorItem.class);
        lenient().when(item.getSignguCode()).thenReturn(signguCode);
        lenient().when(item.getTouristDivisionCode()).thenReturn(touDivCd);
        lenient().when(item.getTouristCount()).thenReturn(touNum);
        return item;
    }
}
