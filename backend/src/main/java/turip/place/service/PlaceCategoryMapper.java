package turip.place.service;

import java.util.Arrays;
import java.util.List;
import turip.place.domain.GoogleMapCategory;
import turip.place.domain.KakaoMapCategory;

public class PlaceCategoryMapper {

    public static String parseCategory(String category, MapProvider provider) {
        String[] categories = category.split(" > ");
        List<String> parsedCategories;

        if (provider == MapProvider.KAKAO) {
            parsedCategories = Arrays.stream(categories)
                    .map(KakaoMapCategory::parseCategoryName)
                    .toList();
        } else {
            parsedCategories = Arrays.stream(categories)
                    .map(GoogleMapCategory::parseCategoryName)
                    .toList();
        }

        return String.join(" > ", parsedCategories);
    }
}
