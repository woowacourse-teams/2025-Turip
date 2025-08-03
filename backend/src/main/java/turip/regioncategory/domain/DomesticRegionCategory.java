package turip.regioncategory.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    public static boolean containsName(String name) {
        for (DomesticRegionCategory category : values()) {
            if (name.contains(category.displayName)) {
                return true;
            }
        }
        return false;
    }
}
