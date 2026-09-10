package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import turip.region.domain.DomesticRegionCategory;
import turip.region.domain.RegionPopularitySnapshot;

public record PopularDestinationsResponse(
        @JsonProperty("baseMonth")
        String baseMonth,

        @JsonProperty("destinations")
        List<PopularDestination> destinations
) {

    private static final int TOP_LIMIT = 10;

    public static PopularDestinationsResponse from(RegionPopularitySnapshot snapshot) {
        List<Map.Entry<DomesticRegionCategory, Long>> ranked = snapshot.categoryCounts().entrySet().stream()
                .sorted(Map.Entry.<DomesticRegionCategory, Long>comparingByValue().reversed())
                .limit(TOP_LIMIT)
                .toList();

        List<PopularDestination> destinations = new ArrayList<>();
        int rank = 1;
        for (Map.Entry<DomesticRegionCategory, Long> entry : ranked) {
            destinations.add(new PopularDestination(rank++, entry.getKey().getDisplayName(), entry.getValue()));
        }

        return new PopularDestinationsResponse(snapshot.baseMonth(), destinations);
    }

    public record PopularDestination(
            @JsonProperty("rank")
            int rank,

            @JsonProperty("regionCategory")
            String regionCategory,

            @JsonProperty("visitorCount")
            long visitorCount
    ) {
    }
}
