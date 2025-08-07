package turip.regioncategory.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.city.domain.City;
import turip.city.service.CityService;
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
    private static final String DOMESTIC_ETC_IMAGE_URL = "https://drive.google.com/uc?export=view&id=1J_jSoyc5v2EXm_y3n4hfnGqljDY_l7MQ";
    private static final String OVERSEAS_ETC_IMAGE_URL = "https://drive.google.com/uc?export=view&id=1yWs2EI8tSHSejxdaNXxZefNIyih_yMYq";

    private final CityService cityService;
    private final CountryService countryService;

    public RegionCategoriesResponse findRegionCategoriesByCountryType(boolean isDomestic) {
        if (isDomestic) {
            return RegionCategoriesResponse.from(findDomesticRegionCategories());
        }

        return RegionCategoriesResponse.from(findOverseasRegionCategories());
    }

    private List<RegionCategoryResponse> findDomesticRegionCategories() {
        List<RegionCategoryResponse> results = new ArrayList<>();
        List<City> cities = cityService.findCitiesByCountryName(KOREA_COUNTRY_NAME);

        for (City city : cities) {
            if (DomesticRegionCategory.containsName(city.getName())) {
                results.add(RegionCategoryResponse.from(city));
            }
        }

        results.add(RegionCategoryResponse.of(DOMESTIC_ETC_NAME, DOMESTIC_ETC_IMAGE_URL));
        return results;
    }

    private List<RegionCategoryResponse> findOverseasRegionCategories() {
        List<RegionCategoryResponse> results = new ArrayList<>();
        List<Country> countries = countryService.findOverseasCountries();

        for (Country country : countries) {
            if (OverseasRegionCategory.containsName(country.getName())) {
                results.add(RegionCategoryResponse.from(country));
            }
        }

        results.add(RegionCategoryResponse.of(OVERSEAS_ETC_NAME, OVERSEAS_ETC_IMAGE_URL));
        return results;
    }
}
