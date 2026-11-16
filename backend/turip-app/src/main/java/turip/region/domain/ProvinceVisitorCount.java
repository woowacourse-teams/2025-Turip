package turip.region.domain;

/**
 * 시도(광역지자체) 단위 방문 인원수 (외지인 + 외국인)
 * 히트맵용으로 지원 여부와 무관하게 전체 시도를 담는다.
 *
 * @param areaCode      법정동 시도 코드 (예: 11 서울, 26 부산)
 * @param areaName      시도명 (예: 서울특별시) - 방문자 수 API 응답값을 그대로 사용
 * @param visitorCount  최근 한 달 방문 인원수 (외지인 + 외국인)
 */
public record ProvinceVisitorCount(
        int areaCode,
        String areaName,
        long visitorCount
) {
}
