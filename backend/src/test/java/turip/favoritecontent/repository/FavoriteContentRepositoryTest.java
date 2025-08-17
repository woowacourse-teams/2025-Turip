package turip.favoritecontent.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.favoritecontent.domain.FavoriteContent;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.province.domain.Province;
import turip.province.repository.ProvinceRepository;

@DataJpaTest
class FavoriteContentRepositoryTest {

    @Autowired
    private FavoriteContentRepository favoriteContentRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private CityRepository cityRepository;


    @Test
    @DisplayName("컨텐츠를 최신 찜 기준으로 정렬하여 반환한다")
    void findMyFavoriteContentsByDeviceFid() {
        // given
        Creator creator = new Creator("여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country("대한민국", "대한민국 사진 경로");
        Province province = new Province("강원도");

        Creator savedCreator = creatorRepository.save(creator);
        Country savedCountry = countryRepository.save(country);
        Province savedProvince = provinceRepository.save(province);
        City city = cityRepository.save(new City(savedCountry, savedProvince, "속초", "속초 이미지 경로"));

        Content content1 = contentRepository.save(
                new Content(savedCreator, city, "뭉치의 속초 브이로그 1편", "속초 브이로그 Url 1", LocalDate.of(2025, 7, 8)));
        Content content2 = contentRepository.save(
                new Content(savedCreator, city, "뭉치의 속초 브이로그 2편", "속초 브이로그 Url 2", LocalDate.of(2025, 7, 8)));

        Member member = memberRepository.save(new Member("testDeviceFid"));

        FavoriteContent favoriteContent1 = new FavoriteContent(LocalDate.now().minusDays(2), member, content1);
        FavoriteContent favoriteContent2 = new FavoriteContent(LocalDate.now().minusDays(1), member,
                content2); // 최신 찜 컨텐츠

        favoriteContentRepository.save(favoriteContent1);
        favoriteContentRepository.save(favoriteContent2);

        // when
        Slice<Content> result = favoriteContentRepository.findMyFavoriteContentsByDeviceFid(
                member.getDeviceFid(), Long.MAX_VALUE, PageRequest.of(0, 10));
        List<Content> contents = result.getContent();

        // then
        assertThat(contents.get(0).getId()).isEqualTo(content2.getId());
        assertThat(contents.get(1).getId()).isEqualTo(content1.getId());
    }
}
