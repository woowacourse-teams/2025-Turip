package turip.infrastructure.client.dto;

import java.util.List;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;

/**
 * 연관 관광지 API 호출 결과
 * API 호출 성공 여부와 실제 데이터를 분리하여 관리
 */
public record RelatedSpotResult(
        boolean isSuccess,  // API 호출이 성공했는지 (response.isSuccess())
        List<RelatedSpot> spots  // 실제 관광지 목록 (빈 리스트일 수 있음)
) {
    public static RelatedSpotResult success(List<RelatedSpot> spots) {
        return new RelatedSpotResult(true, spots);
    }

    public static RelatedSpotResult failure() {
        return new RelatedSpotResult(false, List.of());
    }
}
