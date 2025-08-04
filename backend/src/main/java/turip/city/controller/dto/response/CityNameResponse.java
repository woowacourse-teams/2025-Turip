package turip.city.controller.dto.response;

import turip.city.domain.City;

public record CityNameResponse(
        String name
) {

    public static CityNameResponse from(City city) {
        return new CityNameResponse(city.getName());
    }
}
