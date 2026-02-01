package turip.region.controller.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import turip.region.domain.Country;

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
