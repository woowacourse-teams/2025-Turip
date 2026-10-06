package turip.region.domain;

import java.util.List;
import java.util.Map;

/**
 * 지역별 관광 인기도(방문 인원수) 스냅샷
 * 월 단위로 갱신되는 방문자 수 데이터를 메모리에 보관하기 위한 불변 값 객체.
 *
 * @param baseMonth         기준월 (yyyyMM), 데이터가 없으면 null
 * @param provinceVisitors  전체 시도(17개) 방문 인원수 - 히트맵용 (지원/미지원 무관)
 * @param categoryCounts    지원 지역 카테고리(14개)별 방문 인원수 - 인기 여행지 Top N용
 */
public record RegionPopularitySnapshot(
        String baseMonth,
        List<ProvinceVisitorCount> provinceVisitors,
        Map<DomesticRegionCategory, Long> categoryCounts
) {
    public RegionPopularitySnapshot {
        // 방어적 복사로 불변 스냅샷을 보장한다 (발행 이후 내부 상태가 변하지 않도록).
        provinceVisitors = List.copyOf(provinceVisitors);
        categoryCounts = Map.copyOf(categoryCounts);
    }

    public static RegionPopularitySnapshot empty() {
        return new RegionPopularitySnapshot(null, List.of(), Map.of());
    }

    public boolean isEmpty() {
        return baseMonth == null || (provinceVisitors.isEmpty() && categoryCounts.isEmpty());
    }
}
