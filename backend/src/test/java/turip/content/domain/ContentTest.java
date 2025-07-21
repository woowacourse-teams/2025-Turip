package turip.content.domain;

import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import turip.tripcourse.domain.TripCourse;

class ContentTest {

    @DisplayName("해당 컨텐츠 여행 일수를 확인할 수 있다")
    @Test
    void getTripDurationDays1() {
        // given
        int firstVisitDay = 1;
        int secondVisitDay = 2;
        int visitOrder = 1;

        TripCourse firstDayCourse = new TripCourse(firstVisitDay, visitOrder, null);
        TripCourse secondDayCourse = new TripCourse(secondVisitDay, visitOrder, null);
        Content content = new Content(null, null, List.of(firstDayCourse, secondDayCourse), null, null, null);

        // when & then
        Assertions.assertThat(content.getTripDurationDays())
                .isEqualTo(2);
    }

    @DisplayName("해당 컨텐츠 여행 코스 정보가 존재하지 않는 경우, 여행 일수는 0을 반환한다")
    @Test
    void getTripDurationDays2() {
        // given
        Content content = new Content(null, null, List.of(), null, null, null);

        // when & then
        Assertions.assertThat(content.getTripDurationDays())
                .isEqualTo(0);
    }
}
