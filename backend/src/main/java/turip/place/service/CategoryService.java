package turip.place.service;

import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import turip.place.domain.Category;
import turip.place.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private MapProvider getProviderFromCategoryName(String categoryName) {
        for (char c : categoryName.toCharArray()) {
            if (Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_SYLLABLES ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_JAMO ||
                Character.UnicodeBlock.of(c) == Character.UnicodeBlock.HANGUL_COMPATIBILITY_JAMO) {
                return MapProvider.KAKAO;
            }
        }
        return MapProvider.GOOGLE;
    }

    @Transactional
    public void updateContentPlaceCategoryLanguage() {
        try (Stream<Category> stream = categoryRepository.streamAll()) {
            stream.forEach(category -> {
                MapProvider provider = getProviderFromCategoryName(category.getName());
                String parsedCategory = PlaceCategoryMapper.parseCategory(category.getName(), provider);
                category.updateName(parsedCategory);
            });
        }
    }

    @Transactional
    public Category findOrCreateCategory(String categoryName) {
        MapProvider provider = getProviderFromCategoryName(categoryName);
        String parsedCategoryName = PlaceCategoryMapper.parseCategory(categoryName, provider);

        // db에 변환된 카테고리가 존재하는 경우, 해당 카테고리 사용
        Optional<Category> parsedExistingCategory = categoryRepository.findByName(parsedCategoryName);
        if (parsedExistingCategory.isPresent()) {
            return parsedExistingCategory.get();
        }

        // db에 변환되지 않은 카테고리가 존재하는 경우, 해당 카테고리를 한국어로 변환
        Optional<Category> unparsedExistingCategory = categoryRepository.findByName(categoryName);
        if (unparsedExistingCategory.isPresent()) {
            Category unparsedCategory = unparsedExistingCategory.get();
            unparsedCategory.updateName(parsedCategoryName);
            return unparsedCategory;
        }

        // db에 카테고리가 존재하지 않는 경우, 카테고리 생성 및 저장
        Category category = new Category(parsedCategoryName);
        return categoryRepository.save(category);
    }
}
