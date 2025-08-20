package turip.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.common.exception.custom.NotFoundException;
import turip.content.controller.dto.response.ContentPlaceDetailResponse;
import turip.content.domain.Content;
import turip.content.domain.ContentPlace;
import turip.content.repository.ContentPlaceRepository;
import turip.content.repository.ContentRepository;
import turip.creator.domain.Creator;
import turip.favorite.repository.FavoritePlaceRepository;
import turip.member.domain.Member;
import turip.place.domain.Category;
import turip.place.domain.Place;
import turip.region.domain.City;
import turip.region.domain.Country;
import turip.region.domain.Province;

@ExtendWith(MockitoExtension.class)
class ContentPlaceServiceTest {

    @InjectMocks
    private ContentPlaceService contentPlaceService;

    @Mock
    private ContentPlaceRepository contentPlaceRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    @DisplayName("여행 코스의 방문 장소 수를 계산할 수 있다")
    @Test
    void calculateTripPlaceCount() {
        // given
        int firstVisitDay = 1;
        int secondVisitDay = 2;
        int visitOrder = 1;
        long contentId = 1L;

        Place place1 = new Place(
                1L,
                "경포대 해수욕장",
                "https://naver.me/5UrZAIeY",
                "경포대 해수욕장의 도로명 주소",
                38.1234,
                127.23123
        );
        place1.addCategory(new Category("관광지"));
        Place place2 = new Place(
                2L,
                "하우스멜",
                "https://naver.me/test",
                "하우스멜의 도로명 주소",
                38.0001,
                127.12345
        );
        place2.addCategory(new Category("빵집"));
        place2.addCategory(new Category("카페"));

        LocalTime firstDayTimeLine = LocalTime.parse("00:11:30");
        LocalTime secondDayTimeLine = LocalTime.parse("00:13:30");

        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url", LocalDate.of(2025, 7, 8));

        ContentPlace firstContentPlace = new ContentPlace(firstVisitDay, visitOrder, firstDayTimeLine, place1, content);
        ContentPlace secondContentPlace = new ContentPlace(secondVisitDay, visitOrder, secondDayTimeLine, place2,
                content);
        Member member = new Member(1L, "deviceFid");

        given(contentRepository.existsById(contentId))
                .willReturn(true);
        given(contentPlaceRepository.findAllByContentId(contentId))
                .willReturn(List.of(firstContentPlace, secondContentPlace));
        given(favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member, place1))
                .willReturn(false);
        given(favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member, place2))
                .willReturn(false);
        ContentPlaceDetailResponse response = contentPlaceService.findContentPlaceDetails(member, contentId);

        // when & then
        assertThat(response.contentPlaceCount()).isEqualTo(2);
    }

    @DisplayName("여행 코스 장소들의 타임 라인을 확인할 수 있다.")
    @Test
    void checkTimeLine() {
        // given
        int firstVisitDay = 1;
        int secondVisitDay = 2;
        int visitOrder = 1;
        long contentId = 1L;

        Place place1 = new Place(
                1L,
                "경포대 해수욕장",
                "https://naver.me/5UrZAIeY",
                "경포대 해수욕장의 도로명 주소",
                38.1234,
                127.23123
        );
        place1.addCategory(new Category("관광지"));
        Place place2 = new Place(
                2L,
                "하우스멜",
                "https://naver.me/test",
                "하우스멜의 도로명 주소",
                38.0001,
                127.12345
        );
        place2.addCategory(new Category("빵집"));
        place2.addCategory(new Category("카페"));

        LocalTime firstDayTimeLine = LocalTime.parse("00:11:30");
        LocalTime secondDayTimeLine = LocalTime.parse("00:13:30");

        Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
        Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
        Province province = new Province(1L, "강원도");
        City city = new City(1L, country, province, "속초", "시 이미지 경로");
        Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url", LocalDate.of(2025, 7, 8));

        ContentPlace firstContentPlace = new ContentPlace(firstVisitDay, visitOrder, firstDayTimeLine, place1, content);
        ContentPlace secondContentPlace = new ContentPlace(secondVisitDay, visitOrder, secondDayTimeLine, place2,
                content);
        Member member = new Member(1L, "deviceFid");

        given(contentRepository.existsById(contentId))
                .willReturn(true);
        given(contentPlaceRepository.findAllByContentId(contentId))
                .willReturn(List.of(firstContentPlace, secondContentPlace));
        given(favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member, place1))
                .willReturn(false);
        given(favoritePlaceRepository.existsByFavoriteFolderMemberAndPlace(member, place2))
                .willReturn(false);
        ContentPlaceDetailResponse response = contentPlaceService.findContentPlaceDetails(member, contentId);

        // when & then
        assertThat(response.contentPlaces().get(0).timeLine()).isEqualTo(LocalTime.parse("00:11:30"));
        assertThat(response.contentPlaces().get(1).timeLine()).isEqualTo(LocalTime.parse("00:13:30"));
    }

    @DisplayName("contentId에 대한 컨텐츠가 존재하지 않는 경우 NotFoundException을 발생시킨다")
    @Test
    void validateContentExists() {
        // given
        Long contentId = 1L;
        given(contentRepository.existsById(contentId))
                .willReturn(false);
        Member member = new Member(1L, "deviceFid");

        // when & then
        assertThatThrownBy(() -> contentPlaceService.findContentPlaceDetails(member, contentId))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("여행 코스의 여행 기간 계산")
    @Nested
    class CalculateDurationDays {

        @DisplayName("여행 코스의 여행 기간을 계산할 수 있다")
        @Test
        void calculateDurationDays1() {
            // given
            int firstVisitDay = 1;
            int secondVisitDay = 2;
            int visitOrder = 1;
            long contentId = 1L;

            Place place1 = new Place(
                    1L,
                    "경포대 해수욕장",
                    "https://naver.me/5UrZAIeY",
                    "경포대 해수욕장의 도로명 주소",
                    38.1234,
                    127.23123
            );
            place1.addCategory(new Category("관광지"));
            Place place2 = new Place(
                    2L,
                    "하우스멜",
                    "https://naver.me/test",
                    "하우스멜의 도로명 주소",
                    38.0001,
                    127.12345
            );

            LocalTime firstDayTimeLine = LocalTime.parse("11:30");
            LocalTime secondDayTimeLine = LocalTime.parse("13:30");

            Creator creator = new Creator(1L, "여행하는 뭉치", "프로필 사진 경로");
            Country country = new Country(1L, "대한민국", "대한민국 사진 경로");
            Province province = new Province(1L, "강원도");
            City city = new City(1L, country, province, "속초", "시 이미지 경로");
            Content content = new Content(contentId, creator, city, "뭉치의 속초 브이로그", "속초 브이로그 Url",
                    LocalDate.of(2025, 7, 8));

            ContentPlace firstContentPlace = new ContentPlace(firstVisitDay, visitOrder, firstDayTimeLine, place1,
                    content);
            ContentPlace secondContentPlace = new ContentPlace(secondVisitDay, visitOrder, secondDayTimeLine, place2,
                    content);

            given(contentPlaceRepository.findAllByContentId(contentId))
                    .willReturn(List.of(firstContentPlace, secondContentPlace));

            // when & then
            assertThat(contentPlaceService.calculateDurationDays(contentId))
                    .isEqualTo(2);
        }

        @DisplayName("여행 코스 정보가 존재하지 않는 경우, 여행 기간은 0을 반환한다")
        @Test
        void calculateDurationDays2() {
            // given
            long contentId = 1L;
            given(contentPlaceRepository.findAllByContentId(contentId))
                    .willReturn(List.of());

            // when & then
            assertThat(contentPlaceService.calculateDurationDays(contentId))
                    .isEqualTo(0);
        }
    }
}
