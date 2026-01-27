package turip.place.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import turip.place.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;


}
