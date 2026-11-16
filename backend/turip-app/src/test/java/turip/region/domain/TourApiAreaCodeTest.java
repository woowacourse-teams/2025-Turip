package turip.region.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class TourApiAreaCodeTest {

    @DisplayName("PROVINCE 집계 단위 지역은 시도 전체 카테고리만 반환한다")
    @Test
    void findByVisitorQueryLevelProvince() {
        // when
        List<TourApiAreaCode> provinces = TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.PROVINCE);

        // then
        assertThat(provinces).containsExactlyInAnyOrder(
                TourApiAreaCode.SEOUL,
                TourApiAreaCode.INCHEON,
                TourApiAreaCode.DAEJEON,
                TourApiAreaCode.BUSAN,
                TourApiAreaCode.JEJU,
                TourApiAreaCode.DAEGU
        );
    }

    @DisplayName("CITY 집계 단위 지역은 특정 시군구 카테고리만 반환한다")
    @Test
    void findByVisitorQueryLevelCity() {
        // when
        List<TourApiAreaCode> cities = TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.CITY);

        // then
        assertThat(cities).containsExactlyInAnyOrder(
                TourApiAreaCode.GANGNEUNG,
                TourApiAreaCode.SOKCHO,
                TourApiAreaCode.GYEONGJU,
                TourApiAreaCode.JEONJU,
                TourApiAreaCode.GONGJU,
                TourApiAreaCode.YEOSU,
                TourApiAreaCode.SUWON,
                TourApiAreaCode.GUNSAN
        );
    }

    @DisplayName("집계 단위 조회 결과에는 NOT_FOUND가 포함되지 않는다")
    @Test
    void findByVisitorQueryLevelExcludesNotFound() {
        // when
        List<TourApiAreaCode> provinces = TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.PROVINCE);
        List<TourApiAreaCode> cities = TourApiAreaCode.findByVisitorQueryLevel(VisitorQueryLevel.CITY);

        // then
        assertThat(provinces).doesNotContain(TourApiAreaCode.NOT_FOUND);
        assertThat(cities).doesNotContain(TourApiAreaCode.NOT_FOUND);
    }
}
