package turip.place.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class PlaceCategoryMapperTest {

    @DisplayName("구글맵 api를 이용해 영어로 받아온 카테고리 목록을 한국어로 변환할 수 있다.")
    @ParameterizedTest
    @CsvSource({
            "establishment > store > point_of_interest > home_goods_store > home_improvement_store > furniture_store > clothing_store, 시설/기관 > 상점 > 관심 지점(POI) > 가정용품점 > 홈 인프루브먼트 스토어 > 가구점 > 의류점",
            "establishment > store > point_of_interest > food > sporting_goods_store > food_store > home_goods_store > market > home_improvement_store > furniture_store > discount_store, 시설/기관 > 상점 > 관심 지점(POI) > 음식 > 스포츠 용품점 > 식료품점 > 가정용품점 > 시장 > 홈 인프루브먼트 스토어 > 가구점 > 할인점",
            "establishment > store > point_of_interest > food > food_store > cafe > coffee_shop, 시설/기관 > 상점 > 관심 지점(POI) > 음식 > 식료품점 > 카페 > 커피숍",
            "establishment > store > point_of_interest > food > food_store > confectionery > dessert_shop > ice_cream_shop > food_delivery > meal_delivery > juice_shop, 시설/기관 > 상점 > 관심 지점(POI) > 음식 > 식료품점 > 과자점 > 디저트 가게 > 아이스크림 가게 > 음식 배달 > 음식 배달 > 주스 전문점",
            "category_not_exists, category_not_exists",
    })
    void parseCategory(String category, String expected) {
        // given & when
        String parsedCategory = PlaceCategoryMapper.parseCategory(category);

        // then
        Assertions.assertThat(parsedCategory)
                .isEqualTo(expected);

    }
}
