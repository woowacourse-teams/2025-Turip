package turip.place.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static turip.place.domain.GoogleMapCategory.ESTABLISHMENT;
import static turip.place.domain.GoogleMapCategory.HOME_GOODS_STORE;
import static turip.place.domain.GoogleMapCategory.RESTAURANT;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
        given(categoryRepository.streamAll())
                .willReturn(englishCategories.stream());

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

    @Nested
    class FindOrCreateCategory {

        @DisplayName("한국어로 변환된 카테고리가 저장되어 있는 경우, 해당 카테고리를 리턴한다")
        @Test
        void findOrCreateCategory1() {
            // given
            String parsedCategoryName = RESTAURANT.getKoreanCategoryName();
            given(categoryRepository.findByName(parsedCategoryName))
                    .willReturn(Optional.of(new Category(parsedCategoryName)));

            // when
            Category category = categoryService.findOrCreateCategory(parsedCategoryName);

            // then
            assertThat(category.getName())
                    .isEqualTo(parsedCategoryName);
        }

        @DisplayName("한국어로 변환되지 않은 카테고리가 저장되어 있는 경우, 해당 카테고리를 변환한 후 리턴한다")
        @Test
        void findOrCreateCategory2() {
            // given
            String parsedCategoryName = RESTAURANT.getKoreanCategoryName();
            String unparsedCategoryName = RESTAURANT.getEnglishCategoryName();

            given(categoryRepository.findByName(parsedCategoryName))
                    .willReturn(Optional.empty());

            given(categoryRepository.findByName(unparsedCategoryName))
                    .willReturn(Optional.of(new Category(unparsedCategoryName)));

            // when
            Category category = categoryService.findOrCreateCategory(unparsedCategoryName);

            // then
            assertThat(category.getName())
                    .isEqualTo(parsedCategoryName);
        }

        @DisplayName("카테고리가 존재하지 않는 경우, 새로운 카테고리를 생성하고 리턴한다")
        @Test
        void findOrCreateCategory3() {
            // given
            String parsedCategoryName = RESTAURANT.getKoreanCategoryName();
            String unparsedCategoryName = RESTAURANT.getEnglishCategoryName();

            given(categoryRepository.findByName(parsedCategoryName))
                    .willReturn(Optional.empty());
            given(categoryRepository.findByName(unparsedCategoryName))
                    .willReturn(Optional.empty());
            given(categoryRepository.save(new Category(parsedCategoryName)))
                    .willReturn(new Category(1L, parsedCategoryName));

            // when
            Category category = categoryService.findOrCreateCategory(unparsedCategoryName);

            // then
            assertThat(category.getName())
                    .isEqualTo(parsedCategoryName);
        }
    }
}
