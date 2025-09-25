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

    @Transactional
    public void updateContentPlaceCategoryLanguage() {
        try (Stream<Category> stream = categoryRepository.streamAll()) {
            stream.forEach(category -> {
                String parsedCategory = PlaceCategoryMapper.parseCategory(category.getName());
                category.updateName(parsedCategory);
            });
        }
    }

    @Transactional
    public Category findOrCreateCategory(String categoryName) {
        String parsedCategoryName = PlaceCategoryMapper.parseCategory(categoryName);

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
