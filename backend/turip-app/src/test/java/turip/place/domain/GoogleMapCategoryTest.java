package turip.place.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class GoogleMapCategoryTest {

    @DisplayName("api를 통해 받아온 장소 카테고리를 한국어로 변환할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "restaurant, 🍽️ 식당",
            "library, 📚 도서관",
            "category_not_exists, category_not_exists"
    })
    void parseCategoryName(String englishCategoryName, String expected) {
        // given & when
        String parsedCategoryName = GoogleMapCategory.parseCategoryName(englishCategoryName);

        // then
        assertThat(parsedCategoryName)
                .isEqualTo(expected);
    }
}
