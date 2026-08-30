package turip.region.service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import turip.infrastructure.client.KoreaTourismVisitorClient;
import turip.infrastructure.client.dto.KoreaTourismVisitorResponse.VisitorItem;
import turip.infrastructure.client.dto.VisitorFetchResult;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.ProvinceVisitorCount;
import turip.region.domain.RegionPopularitySnapshot;
import turip.region.domain.TourApiAreaCode;
import turip.region.domain.VisitorQueryLevel;

/**
 * 지역별 관광 인기도(방문 인원수) 조회/캐싱 서비스
 * 방문자 수 API(광역/기초)를 호출해 두 가지를 집계하고 불변 스냅샷으로 보관한다.
 * - 전체 시도(17개) 방문 인원수: 히트맵용 (지원/미지원 무관, 광역 metco)
 * - 지원 지역 카테고리(14개)별 방문 인원수: 인기 여행지 Top N용 (시도는 metco, 시는 기초 locgo)
 * 스냅샷은 스케줄러가 월 1회 갱신하며, 비어있으면 지연 로딩한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegionPopularityService {

    // 관광객 구분 코드 - 2: 외지인, 3: 외국인 (1: 현지인은 제외)
    private static final Set<String> TOURIST_DIVISION_CODES = Set.of("2", "3");
    private static final DateTimeFormatter YEAR_MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    private static final DateTimeFormatter YMD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final KoreaTourismVisitorClient visitorClient;

    private final AtomicReference<RegionPopularitySnapshot> snapshot =
            new AtomicReference<>(RegionPopularitySnapshot.empty());

    /**
     * 캐시된 인기도 스냅샷을 반환한다. 스냅샷이 비어있으면 동기적으로 한 번 갱신한다.
     */
    public RegionPopularitySnapshot getPopularity() {
        RegionPopularitySnapshot current = snapshot.get();
        if (current.isEmpty()) {
            refresh();
            current = snapshot.get();
        }
        return current;
    }

    /**
     * 방문자 수 API를 호출해 스냅샷을 갱신한다. 갱신에 실패하면 기존 스냅샷을 유지한다.
     */
    public synchronized void refresh() {
        Optional<String> latestBaseMonth = visitorClient.findLatestBaseMonth();
        if (latestBaseMonth.isEmpty()) {
            log.warn("방문자 수 기준월을 찾지 못해 인기도 스냅샷 갱신을 건너뜁니다.");
            return;
        }
        String baseMonth = latestBaseMonth.get();

        YearMonth yearMonth = YearMonth.parse(baseMonth, YEAR_MONTH_FORMATTER);
        String startYmd = yearMonth.atDay(1).format(YMD_FORMATTER);
        String endYmd = yearMonth.atEndOfMonth().format(YMD_FORMATTER);

        List<ProvinceVisitorCount> provinceVisitors = fetchProvinceVisitors(startYmd, endYmd);
        Map<DomesticRegionCategory, Long> categoryCounts = new EnumMap<>(DomesticRegionCategory.class);
        putProvinceCategoryCounts(categoryCounts, provinceVisitors);
        putCityCategoryCounts(categoryCounts, startYmd, endYmd);

        if (provinceVisitors.isEmpty() && categoryCounts.isEmpty()) {
            log.warn("방문자 수 집계 결과가 비어 인기도 스냅샷을 유지합니다. baseMonth={}", baseMonth);
            return;
        }

        snapshot.set(new RegionPopularitySnapshot(baseMonth, provinceVisitors, categoryCounts));
        log.info("지역 인기도 스냅샷 갱신 완료. baseMonth={}, 시도 수={}, 지원 지역 수={}",
                baseMonth, provinceVisitors.size(), categoryCounts.size());
    }

    /**
     * 광역(metco)에서 전체 시도의 방문 인원수를 시도별로 집계한다.
     */
    private List<ProvinceVisitorCount> fetchProvinceVisitors(String startYmd, String endYmd) {
        VisitorFetchResult result = visitorClient.fetchProvinceVisitors(startYmd, endYmd);
        if (!result.isSuccess()) {
            log.warn("광역 방문자 수 조회 실패. 시도 히트맵 데이터를 이번 갱신에서 제외합니다.");
            return List.of();
        }

        // areaCode 기준으로 그룹화 (숫자 코드만), 순서 유지
        Map<String, List<VisitorItem>> groupedByArea = result.items().stream()
                .filter(item -> item.getAreaCode() != null && item.getAreaCode().chars().allMatch(Character::isDigit))
                .collect(Collectors.groupingBy(VisitorItem::getAreaCode, LinkedHashMap::new, Collectors.toList()));

        List<ProvinceVisitorCount> provinceVisitors = new ArrayList<>();
        for (Map.Entry<String, List<VisitorItem>> entry : groupedByArea.entrySet()) {
            int areaCode = Integer.parseInt(entry.getKey());
            String areaName = entry.getValue().get(0).getAreaName();
            long count = sumTouristCounts(entry.getValue());
            provinceVisitors.add(new ProvinceVisitorCount(areaCode, areaName, count));
        }
        return provinceVisitors;
    }

    /**
     * 시도 전체 방문자 수에서 지원하는 시도 카테고리(서울·부산 등)의 값을 채운다.
     */
    private void putProvinceCategoryCounts(
            Map<DomesticRegionCategory, Long> counts,
            List<ProvinceVisitorCount> provinceVisitors
    ) {
        if (provinceVisitors.isEmpty()) {
            return;
        }
        Map<Integer, Long> countByAreaCode = provinceVisitors.stream()
                .collect(Collectors.toMap(ProvinceVisitorCount::areaCode, ProvinceVisitorCount::visitorCount));

        for (TourApiAreaCode areaCode : TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.PROVINCE)) {
            long count = countByAreaCode.getOrDefault(areaCode.getAreaCode(), 0L);
            counts.put(areaCode.getDomesticRegionCategory(), count);
        }
    }

    /**
     * 기초(locgo)에서 지원하는 시 카테고리(강릉 등)의 방문자 수를 signguCode로 필터해 채운다.
     */
    private void putCityCategoryCounts(Map<DomesticRegionCategory, Long> counts, String startYmd, String endYmd) {
        VisitorFetchResult result = visitorClient.fetchCityVisitors(startYmd, endYmd);
        if (!result.isSuccess()) {
            log.warn("기초 방문자 수 조회 실패. 시 단위 지역을 이번 갱신에서 제외합니다.");
            return;
        }

        for (TourApiAreaCode areaCode : TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.CITY)) {
            Set<String> targetSignguCodes = areaCode.getSigunguCodes().stream()
                    .map(String::valueOf)
                    .collect(Collectors.toSet());
            List<VisitorItem> items = result.items().stream()
                    .filter(item -> targetSignguCodes.contains(item.getSignguCode()))
                    .toList();
            counts.put(areaCode.getDomesticRegionCategory(), sumTouristCounts(items));
        }
    }

    private long sumTouristCounts(List<VisitorItem> items) {
        double sum = items.stream()
                .filter(item -> item.getTouristDivisionCode() != null
                        && TOURIST_DIVISION_CODES.contains(item.getTouristDivisionCode()))
                .mapToDouble(item -> item.getTouristCount() == null ? 0.0 : item.getTouristCount())
                .sum();
        return Math.round(sum);
    }
}
