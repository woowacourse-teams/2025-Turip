package turip.content.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.province.domain.Province;
import turip.province.repository.ProvinceRepository;

@DataJpaTest
class ContentRepositoryTest {

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @DisplayName("키워드로 콘텐츠 및 크리에이터 이름을 검색해 개수를 반환하는 메서드 테스트")
    @Nested
    class CountByKeyword {

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들의 수를 확인할 수 있다.")
        @ParameterizedTest
        @CsvSource({
                "메이,3",
                "뭉치,2",
                "대구,2"
        })
        void countByKeyword1(String keyword, int expected) {
            // given
            Creator may = new Creator("여행하는 메이", "프사1");
            Creator moong = new Creator("여행하는 뭉치", "프사2");
            Country country = new Country("대한민국", "대한민국 사진 경로");
            Province province1 = new Province("대구광역시");
            Province province2 = new Province("부산광역시");
            City city1 = new City(country, province1, "수성구", "시 이미지 경로");
            City city2 = new City(country, province2, "해운대구", "시 이미지 경로");
            City city3 = new City(country, null, "서울특별시", "시 이미지 경로");

            Content mayContent1 = new Content(may, city1, "메이의 대구 여행", "url1", LocalDate.of(2025, 7, 8));
            Content mayContent2 = new Content(may, city2, "메이의 부산 여행", "url2", LocalDate.of(2025, 7, 8));
            Content moongAndMayContent = new Content(moong, city3, "뭉치와 메이의 서울 여행", "url3", LocalDate.of(2025, 7, 8));
            Content moongContent = new Content(moong, city1, "뭉치의 대구 여행", "url4", LocalDate.of(2025, 7, 8));

            creatorRepository.saveAll(List.of(may, moong));
            countryRepository.save(country);
            provinceRepository.saveAll(List.of(province1, province2));
            cityRepository.saveAll(List.of(city1, city2, city3));
            contentRepository.saveAll(List.of(mayContent1, mayContent2, moongAndMayContent, moongContent));

            // when
            int count = contentRepository.countByKeywordContaining(keyword);

            // then
            assertThat(count)
                    .isEqualTo(expected);
        }
    }

    @DisplayName("페이지네이션 기반 키워드 검색 메서드 테스트")
    @Nested
    class FindByKeyword {

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들을 확인할 수 있다.")
        @ParameterizedTest
        @CsvSource({
                "메이,3",
                "뭉치,2",
                "대구,2"
        })
        void findByKeyword1(String keyword, int expected) {
            // given
            Creator may = new Creator("여행하는 메이", "프사1");
            Creator moong = new Creator("여행하는 뭉치", "프사2");
            Country country = new Country("대한민국", "대한민국 사진 경로");
            Province province1 = new Province("대구광역시");
            Province province2 = new Province("부산광역시");
            City city1 = new City(country, province1, "수성구", "시 이미지 경로");
            City city2 = new City(country, province2, "해운대구", "시 이미지 경로");
            City city3 = new City(country, null, "서울특별시", "시 이미지 경로");

            Content mayContent1 = new Content(may, city1, "메이의 대구 여행", "url1", LocalDate.of(2025, 7, 8));
            Content mayContent2 = new Content(may, city2, "메이의 부산 여행", "url2", LocalDate.of(2025, 7, 8));
            Content moongAndMayContent = new Content(moong, city3, "뭉치와 메이의 서울 여행", "url3", LocalDate.of(2025, 7, 8));
            Content moongContent = new Content(moong, city1, "뭉치의 대구 여행", "url4", LocalDate.of(2025, 7, 8));

            creatorRepository.saveAll(List.of(may, moong));
            countryRepository.save(country);
            provinceRepository.saveAll(List.of(province1, province2));
            cityRepository.saveAll(List.of(city1, city2, city3));
            contentRepository.saveAll(List.of(mayContent1, mayContent2, moongAndMayContent, moongContent));

            // when
            Slice<Content> contents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(0, 5));

            // then
            assertThat(contents.getContent().size())
                    .isEqualTo(expected);
        }

        @DisplayName("제목 혹은 크리에이터 이름에 키워드가 포함되는 컨텐츠들을 원하는 수만큼만 가져올 수 있다")
        @Test
        void findByKeyword2() {
            // given
            String keyword = "메이";
            Creator creator = new Creator("여행하는 " + keyword, "사람 이미지");
            Country country = new Country("대한민국", "대한민국 사진 경로");
            Province province = new Province("도");
            City city = new City(country, province, "시", "시 이미지 경로");
            Content content1 = new Content(creator, city, keyword + "의 대구 여행", "url 1", LocalDate.of(2025, 7, 8));
            Content content2 = new Content(creator, city, keyword + "의 부산 여행", "url 2", LocalDate.of(2025, 7, 8));
            Content content3 = new Content(creator, city, keyword + "의 서울 여행", "url 3", LocalDate.of(2025, 7, 8));
            Content content4 = new Content(creator, city, keyword + "의 춘천 여행", "url 4", LocalDate.of(2025, 7, 8));
            Content content5 = new Content(creator, city, keyword + "의 강릉 여행", "url 5", LocalDate.of(2025, 7, 8));
            Content content6 = new Content(creator, city, keyword + "의 후쿠오카 여행", "url 6", LocalDate.of(2025, 7, 8));
            Content content7 = new Content(creator, city, keyword + "의 다낭 여행", "url 7", LocalDate.of(2025, 7, 8));

            creatorRepository.save(creator);
            countryRepository.save(country);
            provinceRepository.save(province);
            cityRepository.save(city);
            List<Content> savedContents = contentRepository.saveAll(
                    List.of(content1, content2, content3, content4, content5, content6, content7));

            // when
            int pageSize = 5;
            int totalSize = savedContents.size();
            Slice<Content> firstPageContents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(0, pageSize));
            Slice<Content> secondPageContents = contentRepository.findByKeywordContaining(keyword, Long.MAX_VALUE,
                    PageRequest.of(1, pageSize));

            // then
            Assertions.assertAll(
                    () -> assertThat(firstPageContents.getContent().size())
                            .isEqualTo(pageSize),
                    () -> assertThat(secondPageContents.getContent().size())
                            .isEqualTo(totalSize - pageSize)
            );
        }
    }
}
