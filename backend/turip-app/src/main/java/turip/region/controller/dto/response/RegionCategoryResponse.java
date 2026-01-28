package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.region.domain.City;
import turip.region.domain.Country;

public record RegionCategoryResponse(
        @JsonProperty("regionCategoryName")
        String name,
        CountryResponse country,
        @JsonProperty("regionCategoryImageUrl")
        String imageUrl
) {

    public static RegionCategoryResponse from(City city) {
        return new RegionCategoryResponse(
                city.getName(),
                CountryResponse.from(city.getCountry()),
                city.getImageUrl()
        );
    }

    public static RegionCategoryResponse from(Country country) {
        return new RegionCategoryResponse(
                country.getName(),
                CountryResponse.from(country),
                country.getImageUrl()
        );
    }

    public static RegionCategoryResponse of(String name, String imageUrl) {
        return new RegionCategoryResponse(
                name,
                null,
                imageUrl
        );
    }
} 
