package turip.region.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class RelatedTuripSpotsTest {

    @DisplayName("'국내 기타'를 제외한 모든 지원 지역은 폴백 데이터를 가진다")
    @ParameterizedTest
    @EnumSource(value = DomesticRegionCategory.class, names = "OTHER_DOMESTIC", mode = EnumSource.Mode.EXCLUDE)
    void everySupportedCategoryHasFallback(DomesticRegionCategory category) {
        // when & then
        assertThatCode(() -> RelatedTuripSpots.from(category))
                .doesNotThrowAnyException();
    }

    @DisplayName("폴백 데이터는 카테고리별로 비어있지 않은 장소 목록을 가진다")
    @ParameterizedTest
    @EnumSource(value = DomesticRegionCategory.class, names = "OTHER_DOMESTIC", mode = EnumSource.Mode.EXCLUDE)
    void fallbackHasNonEmptySpots(DomesticRegionCategory category) {
        // when
        RelatedTuripSpots relatedTuripSpots = RelatedTuripSpots.from(category);

        // then
        assertThat(relatedTuripSpots.getCategorySpots()).isNotEmpty();
        relatedTuripSpots.getCategorySpots()
                .values()
                .forEach(spots -> assertThat(spots).isNotEmpty());
    }

    @DisplayName("'국내 기타' 카테고리는 폴백을 지원하지 않아 예외를 던진다")
    @Test
    void otherDomesticThrows() {
        // when & then
        assertThatThrownBy(() -> RelatedTuripSpots.from(DomesticRegionCategory.OTHER_DOMESTIC))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
