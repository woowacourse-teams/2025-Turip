package turip.place.service;

import java.util.List;
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
        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            String parsedCategory = PlaceCategoryMapper.parseCategory(category.getName());
            category.updateName(parsedCategory);
        }
    }
}
