package turip.regioncategory.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.city.domain.City;
import turip.city.service.CityService;
import turip.country.domain.Country;
import turip.country.service.CountryService;
import turip.regioncategory.controller.dto.response.RegionCategoriesResponse;

@ExtendWith(MockitoExtension.class)
class RegionCategoryServiceTest {

    @InjectMocks
    private RegionCategoryService regionCategoryService;

    @Mock
    private CityService cityService;

    @Mock
    private CountryService countryService;

    @DisplayName("국내 지역 카테고리 조회 시 국내 도시 목록과 기타 카테고리를 반환한다")
    @Test
    void getRegionCategoriesByCountryType_withIsKoreaTrue_returnsDomesticCategories() {
        // given
        City seoul = mock(City.class);
        City busan = mock(City.class);
        List<City> cities = List.of(seoul, busan);

        Country korea = mock(Country.class);
        given(korea.getId()).willReturn(1L);
        given(korea.getName()).willReturn("한국");
        given(korea.getImageUrl()).willReturn("https://example.com/korea.jpg");

        given(seoul.getId()).willReturn(1L);
        given(seoul.getName()).willReturn("서울");
        given(seoul.getImageUrl()).willReturn("https://example.com/seoul.jpg");
        given(seoul.getCountry()).willReturn(korea);
        given(busan.getId()).willReturn(2L);
        given(busan.getName()).willReturn("부산");
        given(busan.getImageUrl()).willReturn("https://example.com/busan.jpg");
        given(busan.getCountry()).willReturn(korea);

        given(cityService.getCityByCountryName(CountryService.KOREA_COUNTRY_NAME))
                .willReturn(cities);

        // when
        RegionCategoriesResponse response = regionCategoryService.getRegionCategoriesByCountryType(true);

        // then
        assertThat(response.regionCategories()).hasSize(3);
        assertThat(response.regionCategories().get(0).name()).isEqualTo("서울");
        assertThat(response.regionCategories().get(1).name()).isEqualTo("부산");
        assertThat(response.regionCategories().get(2).name()).isEqualTo("국내 기타");
    }

    @DisplayName("해외 지역 카테고리 조회 시 해외 국가 목록과 기타 카테고리를 반환한다")
    @Test
    void getRegionCategoriesByCountryType_withIsKoreaFalse_returnsOverseasCategories() {
        // given
        Country japan = mock(Country.class);
        Country china = mock(Country.class);
        List<Country> countries = List.of(japan, china);

        given(japan.getId()).willReturn(1L);
        given(japan.getName()).willReturn("일본");
        given(japan.getImageUrl()).willReturn("https://example.com/japan.jpg");
        given(china.getId()).willReturn(2L);
        given(china.getName()).willReturn("중국");
        given(china.getImageUrl()).willReturn("https://example.com/china.jpg");

        given(countryService.getOverseasCountries())
                .willReturn(countries);

        // when
        RegionCategoriesResponse response = regionCategoryService.getRegionCategoriesByCountryType(false);

        // then
        assertThat(response.regionCategories()).hasSize(3);
        assertThat(response.regionCategories().get(0).name()).isEqualTo("일본");
        assertThat(response.regionCategories().get(1).name()).isEqualTo("중국");
        assertThat(response.regionCategories().get(2).name()).isEqualTo("해외 기타");
    }

    @DisplayName("지원하지 않는 도시명은 필터링되어 기타 카테고리만 반환된다")
    @Test
    void getDomesticRegionCategories_withUnsupportedCityName_returnsOnlyEtcCategory() {
        // given
        City unsupportedCity = mock(City.class);
        List<City> cities = List.of(unsupportedCity);

        lenient().when(unsupportedCity.getId()).thenReturn(1L);
        lenient().when(unsupportedCity.getName()).thenReturn("지원하지않는도시");
        lenient().when(unsupportedCity.getImageUrl()).thenReturn("https://example.com/unsupported.jpg");

        given(cityService.getCityByCountryName(CountryService.KOREA_COUNTRY_NAME))
                .willReturn(cities);

        // when
        RegionCategoriesResponse response = regionCategoryService.getRegionCategoriesByCountryType(true);

        // then
        assertThat(response.regionCategories()).hasSize(1);
        assertThat(response.regionCategories().get(0).name()).isEqualTo("국내 기타");
    }

    @DisplayName("지원하지 않는 국가명은 필터링되어 기타 카테고리만 반환된다")
    @Test
    void getOverseasRegionCategories_withUnsupportedCountryName_returnsOnlyEtcCategory() {
        // given
        Country unsupportedCountry = mock(Country.class);
        List<Country> countries = List.of(unsupportedCountry);

        lenient().when(unsupportedCountry.getId()).thenReturn(1L);
        lenient().when(unsupportedCountry.getName()).thenReturn("지원하지않는국가");
        lenient().when(unsupportedCountry.getImageUrl()).thenReturn("https://example.com/unsupported.jpg");

        given(countryService.getOverseasCountries())
                .willReturn(countries);

        // when
        RegionCategoriesResponse response = regionCategoryService.getRegionCategoriesByCountryType(false);

        // then
        assertThat(response.regionCategories()).hasSize(1);
        assertThat(response.regionCategories().get(0).name()).isEqualTo("해외 기타");
    }
} 
