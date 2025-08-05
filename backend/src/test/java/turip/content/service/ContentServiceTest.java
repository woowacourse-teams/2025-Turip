package turip.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
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
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.country.domain.Country;
import turip.creator.domain.Creator;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;
import turip.tripcourse.service.TripCourseService;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private TripCourseService tripCourseService;

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

    @DisplayName("지역별 카테고리 기반 컨텐츠 수 조회 기능 테스트")
    @Nested
    class CountContentByRegionCategory {
        @DisplayName("국내 특정 도시(서울) 조회 시 해당 도시의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withSeoul_returnsSeoulContentCount() {
            // given
            String regionCategory = "서울";
            int expectedCount = 5;
            given(contentRepository.countByCityName(regionCategory))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("해외 특정 국가(일본) 조회 시 해당 국가의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withJapan_returnsJapanContentCount() {
            // given
            String regionCategory = "일본";
            int expectedCount = 3;
            given(contentRepository.countByCityCountryName(regionCategory))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("국내 기타 조회 시 DomesticRegionCategory에 없는 도시들의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withDomesticEtc_returnsOtherDomesticContentCount() {
            // given
            String regionCategory = "국내 기타";
            int expectedCount = 10;
            List<String> domesticCategoryNames = DomesticRegionCategory.getDisplayNamesExcludingEtc();
            given(contentRepository.countByCityNameNotIn(domesticCategoryNames))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }

        @DisplayName("해외 기타 조회 시 OverseasRegionCategory에 없는 국가들의 컨텐츠 수를 반환한다")
        @Test
        void countByRegionCategory_withOverseasEtc_returnsOtherOverseasContentCount() {
            // given
            String regionCategory = "해외 기타";
            int expectedCount = 7;
            List<String> overseasCategoryNames = OverseasRegionCategory.getDisplayNamesExcludingEtc();
            given(contentRepository.countByCountryNameNotIn(overseasCategoryNames))
                    .willReturn(expectedCount);

            // when
            ContentCountResponse response = contentService.countByRegionCategory(regionCategory);

            // then
            assertThat(response.count()).isEqualTo(expectedCount);
        }
    }
}
