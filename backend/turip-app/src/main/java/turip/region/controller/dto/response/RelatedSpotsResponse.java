package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;

/**
 * 지역별 연관 관광지 목록 응답
 */
public record RelatedSpotsResponse(
        @JsonProperty("relatedSpots")
        List<CategoryRelatedSpots> relatedSpots
) {

    public static RelatedSpotsResponse from(List<RelatedSpot> relatedSpots) {
        // relatedCategoryLargeName으로 그룹화
        Map<String, List<String>> groupedByCategory = relatedSpots.stream()
                .collect(Collectors.groupingBy(
                        RelatedSpot::getRelatedCategoryLargeName,
                        Collectors.mapping(
                                RelatedSpot::getRelatedSpotName,
                                Collectors.toList()
                        )
                ));

        // CategoryRelatedSpots 리스트로 변환
        List<CategoryRelatedSpots> categorySpots = groupedByCategory.entrySet().stream()
                .map(entry -> new CategoryRelatedSpots(entry.getKey(), entry.getValue()))
                .toList();

        return new RelatedSpotsResponse(categorySpots);
    }

    public static RelatedSpotsResponse empty() {
        return new RelatedSpotsResponse(List.of());
    }

    /**
     * 카테고리별 연관 관광지 목록
     */
    public record CategoryRelatedSpots(
            @JsonProperty("category")
            String category,

            @JsonProperty("spots")
            List<String> spots
    ) {
    }
}
