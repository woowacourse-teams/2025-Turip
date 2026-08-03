package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;
import turip.region.domain.RelatedTuripSpots;

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

        // CategoryRelatedSpots 리스트로 변환 (카테고리 이름 순으로 정렬)
        List<CategoryRelatedSpots> categorySpots = groupedByCategory.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new CategoryRelatedSpots(entry.getKey(), entry.getValue()))
                .toList();

        return new RelatedSpotsResponse(categorySpots);
    }

    public static RelatedSpotsResponse from(RelatedTuripSpots relatedTuripSpots) {
        List<CategoryRelatedSpots> categorySpots = relatedTuripSpots.getCategorySpots().entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
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
