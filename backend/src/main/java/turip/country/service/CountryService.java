package turip.country.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;

@Service
@RequiredArgsConstructor
public class CountryService {

    public static final String KOREA_COUNTRY_NAME = "대한민국";

    private final CountryRepository countryRepository;

    public List<Country> findOverseasCountries() {
        return countryRepository.findAllByNameNot(KOREA_COUNTRY_NAME);
    }
}
