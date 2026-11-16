package turip.region.service.dto;

import turip.region.controller.dto.response.RelatedSpotsResponse;

/**
 * 연관 관광지 조회 결과.
 * <p>
 * 응답 본문과 그 본문이 어디서 왔는지(외부 API / 폴백)를 함께 전달한다.
 * 본문 스키마를 바꾸지 않고 출처만 따로 넘기기 위한 타입이며,
 * 컨트롤러에서 출처를 응답 헤더로 변환한다.
 */
public record RelatedSpotsResult(
        RelatedSpotsResponse response,
        RelatedSpotsDataSource dataSource
) {

    public static RelatedSpotsResult of(RelatedSpotsResponse response, RelatedSpotsDataSource dataSource) {
        return new RelatedSpotsResult(response, dataSource);
    }
}
