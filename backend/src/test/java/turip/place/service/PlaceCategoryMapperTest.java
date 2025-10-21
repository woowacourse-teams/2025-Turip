package turip.place.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlaceCategoryMapperTest {

    @DisplayName("구글맵 api를 이용해 영어로 받아온 카테고리 목록을 한국어로 변환할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "establishment > store > point_of_interest > home_goods_store > home_improvement_store > furniture_store > clothing_store, 🏢 시설/기관 > 🛒 상점 > 📍 관심 지점(POI) > 🏠 가정용품점 > 🏠 홈 인프루브먼트 스토어 > 🛋️ 가구점 > 👗 의류점",
            "establishment > store > point_of_interest > food > sporting_goods_store, 🏢 시설/기관 > 🛒 상점 > 📍 관심 지점(POI) > 🍽️ 음식 > 🏋️ 스포츠 용품점",
            "category_not_exists, category_not_exists",
    })
    void parseGoogleCategory(String category, String expected) {
        // given & when
        String parsedCategory = PlaceCategoryMapper.parseCategory(category, MapProvider.GOOGLE);

        // then
        Assertions.assertThat(parsedCategory)
                .isEqualTo(expected);

    }

    @DisplayName("카카오맵 api를 이용해 한글로 받아온 카테고리 목록을 이모지가 포함된 한국어로 변환할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "'가정,생활 > 가구판매', '가정,생활 > 🛋️ 가구판매'",
            "음식점 > 카페 > 테마카페 > 디저트카페, 음식점 > ☕ 카페 > ☕ 테마카페 > 🍰 디저트카페",
            "category_not_exists, category_not_exists",
    })
    void parseKakaoCategory(String category, String expected) {
        // given & when
        String parsedCategory = PlaceCategoryMapper.parseCategory(category, MapProvider.KAKAO);

        // then
        Assertions.assertThat(parsedCategory)
                .isEqualTo(expected);

    }
}
