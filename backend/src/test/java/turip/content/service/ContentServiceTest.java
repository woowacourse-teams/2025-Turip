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
import turip.content.controller.dto.response.ContentSearchResponse;
import turip.content.domain.Content;
import turip.content.repository.ContentRepository;
import turip.creator.domain.Creator;
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

            List<Content> contents = List.of(new Content(1L, creator, null, null, null, null),
                    new Content(2L, creator, null, null, null, null));
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
}
