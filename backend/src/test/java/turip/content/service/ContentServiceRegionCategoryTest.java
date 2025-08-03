package turip.content.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.content.controller.dto.response.ContentCountResponse;
import turip.content.repository.ContentRepository;
import turip.regioncategory.domain.DomesticRegionCategory;
import turip.regioncategory.domain.OverseasRegionCategory;

@ExtendWith(MockitoExtension.class)
class ContentServiceRegionCategoryTest {

    @InjectMocks
    private ContentService contentService;

    @Mock
    private ContentRepository contentRepository;

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
        given(contentRepository.countByCountryName(regionCategory))
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
