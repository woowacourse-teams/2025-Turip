package turip.regioncategory.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import turip.city.domain.City;
import turip.city.service.CityService;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.country.service.CountryService;
import turip.regioncategory.controller.dto.response.RegionCategoriesResponse;
import turip.regioncategory.controller.dto.response.RegionCategoryResponse;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;

@Service
@RequiredArgsConstructor
public class RegionCategoryService {

    private static final String KOREA_COUNTRY_NAME = "대한민국";
    private static final String DOMESTIC_ETC_NAME = "국내 기타";
    private static final String OVERSEAS_ETC_NAME = "해외 기타";

    @Value("${region.category.domestic.etc.image-url}")
    private String domesticEtcImageUrl;

    @Value("${region.category.overseas.etc.image-url}")
    private String overseasEtcImageUrl;

    private final CityService cityService;
    private final CountryService countryService;
    private final ContentRepository contentRepository;

    public RegionCategoriesResponse findRegionCategoriesByCountryType(boolean isDomestic) {
        if (isDomestic) {
            return RegionCategoriesResponse.from(findDomesticRegionCategories());
        }
        return RegionCategoriesResponse.from(findOverseasRegionCategories());
    }

    private List<RegionCategoryResponse> findDomesticRegionCategories() {
        List<City> cities = cityService.findCitiesByCountryName(KOREA_COUNTRY_NAME);

        List<RegionCategoryResponse> results = findSupportedCitiesWithContent(cities);

        addDomesticEtcCategoryIfHasContent(cities, results);

        return results;
    }

    private List<RegionCategoryResponse> findSupportedCitiesWithContent(List<City> cities) {
        return cities.stream()
                .filter(this::isSupportedDomesticCity)
                .map(this::createCityWithContentCount)
                .filter(cityWithCount -> cityWithCount.contentCount() > 0)
                .sorted(Comparator.comparing(CityWithContentCount::contentCount).reversed())
                .map(cityWithCount -> RegionCategoryResponse.from(cityWithCount.city()))
                .collect(Collectors.toList());
    }

    private boolean isSupportedDomesticCity(City city) {
        return DomesticRegionCategory.containsName(city.getName());
    }

    private CityWithContentCount createCityWithContentCount(City city) {
        int contentCount = contentRepository.countByCityName(city.getName());
        return new CityWithContentCount(city, contentCount);
    }

    private void addDomesticEtcCategoryIfHasContent(List<City> cities, List<RegionCategoryResponse> results) {
        List<String> supportedCityNames = getSupportedCityNames(cities);
        int domesticEtcCount = contentRepository.countDomesticEtcContents(supportedCityNames);

        if (domesticEtcCount > 0) {
            results.add(RegionCategoryResponse.of(DOMESTIC_ETC_NAME, domesticEtcImageUrl));
        }
    }

    private List<String> getSupportedCityNames(List<City> cities) {
        return cities.stream()
                .filter(this::isSupportedDomesticCity)
                .map(City::getName)
                .collect(Collectors.toList());
    }

    private List<RegionCategoryResponse> findOverseasRegionCategories() {
        List<Country> countries = countryService.findOverseasCountries();

        List<RegionCategoryResponse> supportedCountries = findSupportedCountriesWithContent(countries);
        List<RegionCategoryResponse> results = new ArrayList<>(supportedCountries);

        addOverseasEtcCategoryIfHasContent(countries, results);

        return results;
    }

    private List<RegionCategoryResponse> findSupportedCountriesWithContent(List<Country> countries) {
        return countries.stream()
                .filter(this::isSupportedOverseasCountry)
                .map(this::createCountryWithContentCount)
                .filter(countryWithCount -> countryWithCount.contentCount() > 0)
                .sorted(Comparator.comparing(CountryWithContentCount::contentCount).reversed())
                .map(countryWithCount -> RegionCategoryResponse.from(countryWithCount.country()))
                .collect(Collectors.toList());
    }

    private boolean isSupportedOverseasCountry(Country country) {
        return OverseasRegionCategory.containsName(country.getName());
    }

    private CountryWithContentCount createCountryWithContentCount(Country country) {
        int contentCount = contentRepository.countByCityCountryName(country.getName());
        return new CountryWithContentCount(country, contentCount);
    }

    private void addOverseasEtcCategoryIfHasContent(List<Country> countries, List<RegionCategoryResponse> results) {
        List<String> supportedCountryNames = getSupportedCountryNames(countries);
        int overseasEtcCount = contentRepository.countOverseasEtcContents(supportedCountryNames);

        if (overseasEtcCount > 0) {
            results.add(RegionCategoryResponse.of(OVERSEAS_ETC_NAME, overseasEtcImageUrl));
        }
    }

    private List<String> getSupportedCountryNames(List<Country> countries) {
        return countries.stream()
                .filter(this::isSupportedOverseasCountry)
                .map(Country::getName)
                .collect(Collectors.toList());
    }

    private record CityWithContentCount(City city, int contentCount) {
    }

    private record CountryWithContentCount(Country country, int contentCount) {
    }
}
