package turip;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import turip.city.domain.City;
import turip.city.repository.CityRepository;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.country.repository.CountryRepository;
import turip.creator.domain.Creator;
import turip.creator.repository.CreatorRepository;
import turip.favoritecontent.domain.FavoriteContent;
import turip.favoritecontent.repository.FavoriteContentRepository;
import turip.favoritefolder.domain.FavoriteFolder;
import turip.favoritefolder.repository.FavoriteFolderRepository;
import turip.favoriteplace.domain.FavoritePlace;
import turip.favoriteplace.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.place.domain.Place;
import turip.place.repository.PlaceRepository;

@ActiveProfiles("test")
@SpringBootTest
class SoftDeleteIntegrationTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private CreatorRepository creatorRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private FavoriteContentRepository favoriteContentRepository;

    @Autowired
    private FavoriteFolderRepository favoriteFolderRepository;

    @Autowired
    private FavoritePlaceRepository favoritePlaceRepository;

    private Member member;
    private Content content;
    private Place place;
    private FavoriteContent favoriteContent;
    private FavoriteFolder favoriteFolder;
    private FavoritePlace favoritePlace;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 생성
        member = new Member("test-device-fid");
        member = memberRepository.save(member);

        Country country = new Country("대한민국", "test-country-image.jpg");
        country = countryRepository.save(country);

        City city = new City(country, null, "서울", "test-city-image.jpg");
        city = cityRepository.save(city);

        Creator creator = new Creator("테스트 크리에이터", "test-profile.jpg");
        creator = creatorRepository.save(creator);

        content = new Content(creator, city, "테스트 컨텐츠", "https://test.com", LocalDate.now());
        content = contentRepository.save(content);

        place = new Place("테스트 장소", "https://test.com", "테스트 주소", 37.5665, 126.9780);
        place = placeRepository.save(place);

        favoriteFolder = new FavoriteFolder(member, "테스트 폴더", false);
        favoriteFolder = favoriteFolderRepository.save(favoriteFolder);

        favoriteContent = new FavoriteContent(member, content);
        favoriteContent = favoriteContentRepository.save(favoriteContent);

        favoritePlace = new FavoritePlace(favoriteFolder, place);
        favoritePlace = favoritePlaceRepository.save(favoritePlace);
    }

    @Test
    @DisplayName("soft-delete가 제대로 작동해야 한다")
    void testAllDomainsSoftDelete() {
        // given
        Long memberId = member.getId();
        Long contentId = content.getId();
        Long placeId = place.getId();
        Long favoriteContentId = favoriteContent.getId();
        Long favoriteFolderId = favoriteFolder.getId();
        Long favoritePlaceId = favoritePlace.getId();

        // when - 모든 엔티티 삭제
        favoriteContentRepository.delete(favoriteContent);
        favoritePlaceRepository.delete(favoritePlace);
        favoriteFolderRepository.delete(favoriteFolder);
        contentRepository.delete(content);
        placeRepository.delete(place);
        memberRepository.delete(member);

        // then - 모든 엔티티가 조회되지 않아야 함
        assertThat(memberRepository.findById(memberId)).isEmpty();
        assertThat(contentRepository.findById(contentId)).isEmpty();
        assertThat(placeRepository.findById(placeId)).isEmpty();
        assertThat(favoriteContentRepository.findById(favoriteContentId)).isEmpty();
        assertThat(favoriteFolderRepository.findById(favoriteFolderId)).isEmpty();
        assertThat(favoritePlaceRepository.findById(favoritePlaceId)).isEmpty();
    }

    @Test
    @DisplayName("soft-delete가 적용된 도메인만 @SQLRestriction으로 필터링되어야 한다")
    void testSoftDeleteFiltering() {
        // given
        Member member2 = new Member("test-device-fid-2");
        memberRepository.save(member2);

        FavoriteFolder folder2 = new FavoriteFolder(member2, "폴더 2", false);
        favoriteFolderRepository.save(folder2);

        // when - 일부 엔티티만 삭제
        favoriteContentRepository.delete(favoriteContent);
        favoritePlaceRepository.delete(favoritePlace);
        favoriteFolderRepository.delete(favoriteFolder);

        // then - soft-delete가 적용된 도메인만 필터링되어야 한다.
        List<Member> allMembers = memberRepository.findAll();
        List<FavoriteFolder> allFolders = favoriteFolderRepository.findAll();
        List<FavoriteContent> allFavoriteContents = favoriteContentRepository.findAll();
        List<FavoritePlace> allFavoritePlaces = favoritePlaceRepository.findAll();

        assertThat(allMembers).hasSize(2); // member와 member2
        assertThat(allFolders).hasSize(1);
        assertThat(allFolders.getFirst().getId()).isEqualTo(folder2.getId());
        assertThat(allFavoriteContents).isEmpty();
        assertThat(allFavoritePlaces).isEmpty();
    }

    @Test
    @DisplayName("soft-delete된 엔티티의 deletedAt 시간이 정확하게 설정되어야 한다")
    void testDeletedAtTimestamp() {
        // given
        Long memberId = member.getId();
        Long favoriteContentId = favoriteContent.getId();
        Long favoriteFolderId = favoriteFolder.getId();
        Long favoritePlaceId = favoritePlace.getId();

        // when
        favoriteContentRepository.delete(favoriteContent);
        favoritePlaceRepository.delete(favoritePlace);
        favoriteFolderRepository.delete(favoriteFolder);
        memberRepository.delete(member);

        // then
        // soft-delete가 적용된 도메인만 조회되지 않아야 한다
        assertThat(memberRepository.findById(memberId)).isEmpty();
        assertThat(favoriteContentRepository.findById(favoriteContentId)).isEmpty();
        assertThat(favoriteFolderRepository.findById(favoriteFolderId)).isEmpty();
        assertThat(favoritePlaceRepository.findById(favoritePlaceId)).isEmpty();
    }
}
