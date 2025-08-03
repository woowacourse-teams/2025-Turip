package turip.regionCategory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OverseasRegionCategory {
    JAPAN("일본"),
    CHINA("중국"),
    VIETNAM("베트남"),
    TAIWAN("대만"),
    OTHER_OVERSEAS("해외 기타");

    private final String displayName;

    public static boolean containsName(String name) {
        for (OverseasRegionCategory category : values()) {
            if (name.contains(category.displayName)) {
                return true;
            }
        }
        return false;
    }
}
