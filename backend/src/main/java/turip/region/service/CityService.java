package turip.region.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.region.domain.City;
import turip.region.repository.CityRepository;

@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;

    public List<City> findCitiesByCountryName(String countryName) {
        return cityRepository.findAllByCountryName(countryName);
    }
}
