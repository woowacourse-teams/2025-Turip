package turip.place.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import turip.place.service.CategoryService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @Hidden
    @PatchMapping
    public ResponseEntity<Void> updateContentPlaceCategoryLanguage() {
        categoryService.updateContentPlaceCategoryLanguage();
        return ResponseEntity.noContent().build();
    }
}
