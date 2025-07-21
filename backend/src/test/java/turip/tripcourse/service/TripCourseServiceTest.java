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
import turip.content.domain.Content;
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

        Content content = new Content(null, null, null, null, null);
        TripCourse firstDayCourse = new TripCourse(firstVisitDay, visitOrder, null, content);
        TripCourse secondDayCourse = new TripCourse(secondVisitDay, visitOrder, null, content);

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
}
