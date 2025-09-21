package turip.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static turip.place.domain.GoogleMapCategory.ESTABLISHMENT;
import static turip.place.domain.GoogleMapCategory.HOME_GOODS_STORE;
import static turip.place.domain.GoogleMapCategory.RESTAURANT;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import turip.place.domain.Category;
import turip.place.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @DisplayName("저장된 카테고리 정보가 영어인 경우 한국어로 변환한다")
    @Test
    void updateContentPlaceCategoryLanguage() {
        // given
        Category category1 = new Category(
                ESTABLISHMENT.getEnglishCategoryName() + " > " + HOME_GOODS_STORE.getEnglishCategoryName());
        Category category2 = new Category(RESTAURANT.getEnglishCategoryName());
        Category category3 = new Category("category_not_exists");
        List<Category> englishCategories = List.of(
                category1,
                category2,
                category3
        );
        given(categoryRepository.findAll())
                .willReturn(englishCategories);

        // when
        categoryService.updateContentPlaceCategoryLanguage();

        // then
        assertAll(
                () -> assertThat(category1.getName()).isEqualTo(
                        ESTABLISHMENT.getKoreanCategoryName() + " > " + HOME_GOODS_STORE.getKoreanCategoryName()),
                () -> assertThat(category2.getName()).isEqualTo(RESTAURANT.getKoreanCategoryName()),
                () -> assertThat(category3.getName()).isEqualTo("category_not_exists")
        );
    }
}
