package turip.place.domain;

import java.util.Arrays;
import java.util.List;

public class PlaceCategoryMapper {

    public static String parseCategory(String category) {
        String[] categories = category.split(" > ");
        List<String> parsedCategories = Arrays.stream(categories)
                .map(GoogleMapCategory::parseCategoryName)
                .toList();

        return String.join(" > ", parsedCategories);
    }
}
