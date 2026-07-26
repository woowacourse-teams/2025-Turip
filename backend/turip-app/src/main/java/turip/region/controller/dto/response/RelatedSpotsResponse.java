package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import turip.infrastructure.client.dto.KoreaTourismRelatedSpotResponse.RelatedSpot;

/**
 * 지역별 연관 관광지 목록 응답
 */
public record RelatedSpotsResponse(
        @JsonProperty("relatedSpots")
        List<RelatedSpotResponse> relatedSpots
) {

    public static RelatedSpotsResponse from(List<RelatedSpot> relatedSpots) {
        List<RelatedSpotResponse> responses = relatedSpots.stream()
                .map(RelatedSpotResponse::from)
                .toList();
        return new RelatedSpotsResponse(responses);
    }

    public static RelatedSpotsResponse empty() {
        return new RelatedSpotsResponse(List.of());
    }

    /**
     * 연관 관광지 응답
     */
    public record RelatedSpotResponse(
            @JsonProperty("touristSpotName")
            String touristSpotName,

            @JsonProperty("areaName")
            String areaName,

            @JsonProperty("relatedSpotName")
            String relatedSpotName,

            @JsonProperty("relatedRegionName")
            String relatedRegionName,

            @JsonProperty("relatedCategoryLargeName")
            String relatedCategoryLargeName,

            @JsonProperty("relatedCategoryMediumName")
            String relatedCategoryMediumName,

            @JsonProperty("relatedCategorySmallName")
            String relatedCategorySmallName,

            @JsonProperty("relatedRank")
            Integer relatedRank
    ) {

        public static RelatedSpotResponse from(RelatedSpot relatedSpot) {
            return new RelatedSpotResponse(
                    relatedSpot.getTouristSpotName(),
                    relatedSpot.getAreaName(),
                    relatedSpot.getRelatedSpotName(),
                    relatedSpot.getRelatedRegionName(),
                    relatedSpot.getRelatedCategoryLargeName(),
                    relatedSpot.getRelatedCategoryMediumName(),
                    relatedSpot.getRelatedCategorySmallName(),
                    relatedSpot.getRelatedRank()
            );
        }
    }
}
