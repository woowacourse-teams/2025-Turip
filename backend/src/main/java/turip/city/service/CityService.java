package turip.city.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.city.domain.City;
import turip.city.repository.CityRepository;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<City> findCitiesByCountryName(String countryName) {
        return cityRepository.findAllByCountryName(countryName);
    }
}
