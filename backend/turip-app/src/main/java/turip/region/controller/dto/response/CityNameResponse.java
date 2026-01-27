package turip.region.controller.dto.response;

import turip.region.domain.City;

public record CityNameResponse(
        String name
) {

    public static CityNameResponse from(City city) {
        return new CityNameResponse(city.getName());
    }
}
