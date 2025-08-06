package turip.country.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.country.domain.Country;

public record CountryResponse(
        Long id,
        @JsonProperty("countryName")
        String name,
        @JsonProperty("countryImageUrl")
        String imageUrl
) {

    public static CountryResponse from(Country country) {
        return new CountryResponse(
                country.getId(),
                country.getName(),
                country.getImageUrl()
        );
    }
}
