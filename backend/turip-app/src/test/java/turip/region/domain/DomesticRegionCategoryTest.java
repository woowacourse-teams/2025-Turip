package turip.region.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import turip.common.exception.custom.IllegalArgumentException;

class DomesticRegionCategoryTest {

    @DisplayName("국내에 존재하는 도시인지 아닌지 확인할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "서울, true",
            "서울시, true",
            "국내 기타, true",
            "일본, false"
    })
    void containsName(String category, boolean expected) {
        // when & then
        Assertions.assertThat(DomesticRegionCategory.containsName(category))
                .isEqualTo(expected);
    }

    @DisplayName("카테고리가 빈 값인 경우 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void containsName_withBlank_throwsIllegalArgumentException(String category) {
        // when & then
        assertThatThrownBy(() -> DomesticRegionCategory.containsName(category))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("국내 기타를 제외한 카테고리를 확인할 수 있다.")
    @Test
    void getDisplayNamesExcludingEtc() {
        // when & then
        assertThat(DomesticRegionCategory.getDisplayNamesExcludingEtc())
                .doesNotContain(DomesticRegionCategory.OTHER_DOMESTIC.getDisplayName());
    }

    @DisplayName("한글 지역명으로 DomesticRegionCategory를 조회할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "서울, SEOUL",
            "부산, BUSAN",
            "제주, JEJU",
            "인천, INCHEON",
            "대전, DAEJEON",
            "전주, JEONJU",
            "강릉, GANGNEUNG",
            "속초, SOKCHO",
            "경주, GYEONGJU"
    })
    void fromDisplayName1(String displayName, DomesticRegionCategory expected) {
        // given & when
        DomesticRegionCategory result = DomesticRegionCategory.fromDisplayName(displayName);

        // then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 지역명으로 조회 시 예외를 발생시킨다.")
    @ParameterizedTest
    @ValueSource(strings = {"공주", "파리", "존재하지않는지역"})
    void fromDisplayName2(String invalidName) {
        // when & then
        assertThatThrownBy(() -> DomesticRegionCategory.fromDisplayName(invalidName))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
