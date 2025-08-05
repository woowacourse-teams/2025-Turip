package turip.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;
import turip.city.domain.City;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.controller.dto.response.WeeklyPopularFavoriteContentResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.creator.domain.Creator;
import turip.favorite.domain.Favorite;
import turip.favorite.repository.FavoriteRepository;
import turip.member.domain.Member;
import turip.member.repository.MemberRepository;
import turip.tripcourse.service.TripCourseService;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private TripCourseService tripCourseService;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private MemberRepository memberRepository;

    @DisplayName("키워드 기반 검색 기능 테스트")
    @Nested
    class FindContentsByKeyword {

        @DisplayName("lastContentId가 0인 경우, lastContentId가 Long.MAX_VALUE로 치환되어 id가 가장 큰 컨텐츠부터 순서대로 pageSize만큼 읽어온다.")
        @Test
        void findContentsByKeyword1() {
            // given
            String keyword = "메이";
            long lastContentId = 0L;
            int pageSize = 2;
            Long maxId = Long.MAX_VALUE;

            Creator creator = new Creator("메이", null);
            Country korea = new Country("대한민국", null);
            City seoul = new City(korea, null, "서울", null);

            List<Content> contents = List.of(new Content(1L, creator, seoul, null, null, null),
                    new Content(2L, creator, seoul, null, null, null));
            given(contentRepository.findByKeywordContaining(keyword, maxId, PageRequest.of(0, pageSize)))
                    .willReturn(new SliceImpl<>(contents));

            // when
            ContentSearchResponse contentsByKeyword = contentService.searchContentsByKeyword(keyword, pageSize,
                    lastContentId);

            // then
            assertThat(contentsByKeyword.loadable())
                    .isFalse();
        }
    }

    @DisplayName("주간 인기 찜 컨텐츠 조회 테스트")
    @Nested
    class FindWeeklyPopularFavoriteContents {

        @Test
        void findWeeklyPopularFavoriteContents() {
            // given
            LocalDate startDate = LocalDate.now().minusWeeks(1).with(DayOfWeek.MONDAY);
            LocalDate endDate = startDate.plusDays(6);
            int topContentSize = 2;

            Creator creator = new Creator("하루", null);
            Country korea = new Country("대한민국", null);
            City seoul = new City(korea, null, "서울", null);

            Content content1 = new Content(1L, creator, seoul, null, null, null);
            Content content2 = new Content(2L, creator, seoul, null, null, null);

            Member member = new Member(1L, "testDeviceFid");

            List<Content> popularContents = List.of(content1, content2);

            given(favoriteRepository.findTop5PopularContentsByFavoriteBetweenDates(startDate, endDate, topContentSize))
                    .willReturn(popularContents);
            given(memberRepository.findByDeviceFid("testDeviceFid"))
                    .willReturn(Optional.of(member));
            given(favoriteRepository.findByMemberIdAndContentIdIn(1L, List.of(1L, 2L)))
                    .willReturn(List.of(new Favorite(LocalDate.now().minusWeeks(1), member, content1),
                            new Favorite(LocalDate.now().minusWeeks(1), member, content2)));
            given(tripCourseService.calculateDurationDays(content1.getId()))
                    .willReturn(3); // content1, 2박 3일
            given(tripCourseService.calculateDurationDays(content2.getId()))
                    .willReturn(2); // content2, 1박 2일

            // when
            List<WeeklyPopularFavoriteContentResponse> responses = contentService.findWeeklyPopularFavoriteContents(
                    "testDeviceFid", topContentSize);

            // then
            assertThat(responses).hasSize(2);
            assertThat(responses.getFirst().content().id()).isEqualTo(1L);
            assertThat(responses.getFirst().content().isFavorite()).isTrue();
            assertThat(responses.getFirst().tripDuration().days()).isEqualTo(3);

            assertThat(responses.get(1).content().id()).isEqualTo(2L);
            assertThat(responses.get(1).content().isFavorite()).isTrue();
            assertThat(responses.get(1).tripDuration().days()).isEqualTo(2);
        }
    }
}
