package turip.regioncategory.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.city.domain.City;
import turip.country.controller.dto.CountryResponse;
import turip.country.domain.Country;

public record RegionCategoryResponse(
        Long id,
        @JsonProperty("regionCategoryName")
        String name,
        CountryResponse country,
        @JsonProperty("regionCategoryImageUrl")
        String imageUrl
) {

    public static RegionCategoryResponse from(City city) {
        return new RegionCategoryResponse(
                city.getId(),
                city.getName(),
                CountryResponse.from(city.getCountry()),
                city.getImageUrl()
        );
    }

    public static RegionCategoryResponse from(Country country) {
        return new RegionCategoryResponse(
                country.getId(),
                country.getName(),
                CountryResponse.from(country),
                country.getImageUrl()
        );
    }

    public static RegionCategoryResponse of(String name, String imageUrl) {
        return new RegionCategoryResponse(
                null,
                name,
                null,
                imageUrl
        );
    }
} 
