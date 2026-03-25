package turip.region.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.region.domain.City;
import turip.region.domain.Country;
import turip.region.domain.Province;
import turip.region.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

    @InjectMocks
    private CityService cityService;

    @Mock
    private CityRepository cityRepository;

    @Test
    @DisplayName("모든 도시를 가나다순으로 정렬하여 반환한다")
    void findAll_returnsSortedCities() {
        // given
        Country country = new Country("대한민국", "http://example.com/korea.jpg");
        Province province = new Province("경기도");

        City seoul = new City(country, province, "서울", "http://example.com/seoul.jpg");
        City busan = new City(country, province, "부산", "http://example.com/busan.jpg");
        City daegu = new City(country, province, "대구", "http://example.com/daegu.jpg");

        when(cityRepository.findAll()).thenReturn(List.of(seoul, daegu, busan));

        // when
        List<City> cities = cityService.findAll();

        // then
        assertThat(cities).hasSize(3);
        assertThat(cities.get(0).getName()).isEqualTo("대구");
        assertThat(cities.get(1).getName()).isEqualTo("부산");
        assertThat(cities.get(2).getName()).isEqualTo("서울");
    }
}
