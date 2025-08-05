package turip.regioncategory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import turip.exception.BadRequestException;

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
            throw new BadRequestException("지역 카테고리 명이 빈 값입니다.");
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
