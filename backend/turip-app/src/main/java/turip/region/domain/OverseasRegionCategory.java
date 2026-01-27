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
public enum OverseasRegionCategory {
    JAPAN("일본"),
    CHINA("중국"),
    VIETNAM("베트남"),
    TAIWAN("대만"),
    OTHER_OVERSEAS("해외 기타");

    private final String displayName;

    public static boolean containsName(String regionCategoryName) {
        if (StringUtils.isBlank(regionCategoryName)) {
            throw new IllegalArgumentException(ErrorTag.REGION_CATEGORY_INVALID);
        }

        for (OverseasRegionCategory category : values()) {
            if (regionCategoryName.contains(category.displayName)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> getDisplayNamesExcludingEtc() {
        return Arrays.stream(values())
                .filter(category -> !category.equals(OTHER_OVERSEAS))
                .map(OverseasRegionCategory::getDisplayName)
                .toList();
    }

    public boolean matchesDisplayName(String name) {
        return this.displayName.equals(name);
    }
}
