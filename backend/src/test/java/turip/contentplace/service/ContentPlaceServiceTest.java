package turip.contentplace.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.category.domain.Category;
import turip.contentplace.controller.dto.response.ContentPlaceDetailResponse;
import turip.contentplace.domain.ContentPlace;
import turip.contentplace.repository.ContentPlaceRepository;
import turip.exception.custom.NotFoundException;
import turip.place.domain.Place;

@ExtendWith(MockitoExtension.class)
class ContentPlaceServiceTest {

    @InjectMocks
    private ContentPlaceService contentPlaceService;
    @Mock
    private ContentPlaceRepository contentPlaceRepository;

    @DisplayName("contentId에 대한 컨텐츠가 존재하지 않으면 NotFoundException을 발생시킨다.")
    @Test
    void countByContentId() {
        // given
        long contentId = 1L;
        given(contentPlaceRepository.countByContent_Id(contentId))
                .willThrow(new IllegalArgumentException());

        // when & then
        assertThatThrownBy(() -> contentPlaceService.countByContentId(contentId))
                .isInstanceOf(NotFoundException.class);
    }

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
        ContentPlace firstDayCourse = new ContentPlace(firstVisitDay, visitOrder, place1, null);
        ContentPlace secondDayCourse = new ContentPlace(secondVisitDay, visitOrder, place2, null);

        // when
        given(contentPlaceRepository.findAllByContent_Id(contentId))
                .willReturn(List.of(firstDayCourse, secondDayCourse));
        ContentPlaceDetailResponse response = contentPlaceService.findContentPlaceDetails(contentId);

        // then
        assertThat(response.contentPlaceCount()).isEqualTo(2);
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

            ContentPlace firstDayCourse = new ContentPlace(firstVisitDay, visitOrder, null, null);
            ContentPlace secondDayCourse = new ContentPlace(secondVisitDay, visitOrder, null, null);

            // when
            given(contentPlaceRepository.findAllByContent_Id(contentId))
                    .willReturn(List.of(firstDayCourse, secondDayCourse));

            // then
            assertThat(contentPlaceService.calculateDurationDays(contentId))
                    .isEqualTo(2);
        }

        @DisplayName("여행 코스 정보가 존재하지 않는 경우, 여행 기간은 0을 반환한다")
        @Test
        void calculateDurationDays2() {
            // given
            long contentId = 1L;

            // when
            given(contentPlaceRepository.findAllByContent_Id(contentId))
                    .willReturn(List.of());

            // then
            assertThat(contentPlaceService.calculateDurationDays(contentId))
                    .isEqualTo(0);
        }
    }
}
