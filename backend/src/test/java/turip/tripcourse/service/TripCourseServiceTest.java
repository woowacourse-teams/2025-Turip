package turip.tripcourse.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.place.domain.Place;
import turip.tripcourse.controller.dto.response.TripCourseDetailResponse;
import turip.tripcourse.domain.TripCourse;
import turip.tripcourse.repository.TripCourseRepository;

@ExtendWith(MockitoExtension.class)
class TripCourseServiceTest {
    
    @InjectMocks
    private TripCourseService tripCourseService;
    @Mock
    private TripCourseRepository tripCourseRepository;

    @DisplayName("컨텐츠에 대한 여행 일수를 확인할 수 있다")
    @Test
    void calculateDurationDays1() {
        // given
        int firstVisitDay = 1;
        int secondVisitDay = 2;
        int visitOrder = 1;
        long contentId = 1L;

        TripCourse firstDayCourse = new TripCourse(firstVisitDay, visitOrder, null, null);
        TripCourse secondDayCourse = new TripCourse(secondVisitDay, visitOrder, null, null);

        // when
        given(tripCourseRepository.findAllByContent_Id(contentId))
                .willReturn(List.of(firstDayCourse, secondDayCourse));

        // then
        assertThat(tripCourseService.calculateDurationDays(contentId))
                .isEqualTo(2);
    }

    @DisplayName("컨텐츠 여행 코스 정보가 존재하지 않는 경우, 여행 일수는 0을 반환한다")
    @Test
    void calculateDurationDays2() {
        // given
        long contentId = 1L;

        // when
        given(tripCourseRepository.findAllByContent_Id(contentId))
                .willReturn(List.of());

        // then
        assertThat(tripCourseService.calculateDurationDays(contentId))
                .isEqualTo(0);
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
                127.23123,
                List.of("관광")
        );
        Place place2 = new Place(
                2L,
                "하우스멜",
                "https://naver.me/test",
                "하우스멜의 도로명 주소",
                38.0001,
                127.12345,
                List.of("베이커리", "카페")
        );
        TripCourse firstDayCourse = new TripCourse(firstVisitDay, visitOrder, place1, null);
        TripCourse secondDayCourse = new TripCourse(secondVisitDay, visitOrder, place2, null);

        // when
        given(tripCourseRepository.findAllByContent_Id(contentId))
                .willReturn(List.of(firstDayCourse, secondDayCourse));
        TripCourseDetailResponse response = tripCourseService.findTripCourseDetails(contentId);

        // then
        assertThat(response.tripPlaceCount()).isEqualTo(2);
    }
}
