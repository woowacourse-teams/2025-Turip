package turip.region.domain;

import io.micrometer.common.util.StringUtils;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import turip.common.exception.ErrorTag;
import turip.common.exception.custom.IllegalArgumentException;

@Getter
@RequiredArgsConstructor
public enum DomesticRegionCategory {
    SEOUL("서울"),
    BUSAN("부산"),
    JEJU("제주"),
    INCHEON("인천"),
    DAEJEON("대전"),
    JEONJU("전주"),
    GANGNEUNG("강릉"),
    SOKCHO("속초"),
    GYEONGJU("경주"),
    OTHER_DOMESTIC("국내 기타");

    private final String displayName;

    public static boolean containsName(String regionCategoryName) {
        if (StringUtils.isBlank(regionCategoryName)) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }

        for (DomesticRegionCategory category : values()) {
            if (regionCategoryName.contains(category.displayName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getDisplayNamesExcludingEtc() {
        return Arrays.stream(values())
                .filter(category -> !category.equals(OTHER_DOMESTIC))
                .map(DomesticRegionCategory::getDisplayName)
                .toList();
    }

    public boolean matchesDisplayName(String name) {
        return this.displayName.equals(name);
    }
}
