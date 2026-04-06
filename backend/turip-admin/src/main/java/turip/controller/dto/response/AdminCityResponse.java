package turip.controller.dto.response;

import turip.region.domain.City;

public record AdminCityResponse(
        Long id,
        String name
) {
    public static AdminCityResponse from(City city) {
        return new AdminCityResponse(city.getId(), city.getName());
    }
}
