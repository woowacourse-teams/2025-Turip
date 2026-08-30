package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Comparator;
import java.util.List;
import turip.region.domain.ProvinceVisitorCount;
import turip.region.domain.RegionPopularitySnapshot;

public record RegionPopularityResponse(
        @JsonProperty("baseMonth")
        String baseMonth,

        @JsonProperty("regions")
        List<RegionPopularity> regions
) {

    public static RegionPopularityResponse from(RegionPopularitySnapshot snapshot) {
        List<RegionPopularity> regions = snapshot.provinceVisitors().stream()
                .sorted(Comparator.comparingLong(ProvinceVisitorCount::visitorCount).reversed())
                .map(province -> new RegionPopularity(
                        province.areaCode(),
                        province.areaName(),
                        province.visitorCount()
                ))
                .toList();

        return new RegionPopularityResponse(snapshot.baseMonth(), regions);
    }

    public record RegionPopularity(
            @JsonProperty("areaCode")
            int areaCode,

            @JsonProperty("regionName")
            String regionName,

            @JsonProperty("visitorCount")
            long visitorCount
    ) {
    }
}
