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

    public List<City> getCityByCountryName(String name) {
        return cityRepository.findAllByCountryName(name);
    }
}
