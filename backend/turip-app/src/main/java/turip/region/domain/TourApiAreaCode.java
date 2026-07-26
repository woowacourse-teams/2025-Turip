package turip.region.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 한국관광공사 TourAPI의 지역 코드(areaCode)와 시군구 코드(sigunguCode)를 관리하는 enum 튜립 서버의 DomesticRegionCategory와 매핑하여 연관 관광지 정보 조회에 사용
 */
@Getter
@RequiredArgsConstructor
public enum TourApiAreaCode {

    /**
     * 서울특별시 areaCode: 1
     */
    SEOUL(11, 11110, DomesticRegionCategory.SEOUL),

    /**
     * 인천광역시 areaCode: 2
     */
    INCHEON(2, 18710, DomesticRegionCategory.INCHEON),

    /**
     * 대전광역시 areaCode: 3
     */
    DAEJEON(3, 30140, DomesticRegionCategory.DAEJEON),

    /**
     * 부산광역시 areaCode: 6
     */
    BUSAN(6, 26230, DomesticRegionCategory.BUSAN),

    /**
     * 강원도 > 강릉시 areaCode: 51, sigunguCode: 51159
     */
    GANGNEUNG(51, 51150, DomesticRegionCategory.GANGNEUNG),

    /**
     * 강원도 > 속초시 areaCode: 51, sigunguCode: 51219
     */
    SOKCHO(51, 51210, DomesticRegionCategory.SOKCHO),

    /**
     * 경상북도 > 경주시 areaCode: 47, sigunguCode: 47130
     */
    GYEONGJU(47, 47130, DomesticRegionCategory.GYEONGJU),

    /**
     * 전북특별자치도 > 전주시 areaCode: 52, sigunguCode: 52111
     */
    JEONJU(52, 52111, DomesticRegionCategory.JEONJU),

    /**
     * 제주특별자치도 areaCode: 50
     */
    JEJU(50, 50130, DomesticRegionCategory.JEJU),

    /**
     * 매핑되지 않는 지역 (OTHER_DOMESTIC 등)
     */
    NOT_FOUND(null, null, null);

    /**
     * 한국관광공사 TourAPI의 지역 코드 (시도 단위)
     */
    private final Integer areaCode;

    /**
     * 한국관광공사 TourAPI의 시군구 코드 (시군구 단위) null인 경우 시도 전체를 의미
     */
    private final Integer sigunguCode;

    /**
     * 튜립 서버의 국내 지역 카테고리 null인 경우 현재 튜립에서 미지원하는 지역
     */
    private final DomesticRegionCategory domesticRegionCategory;

    /**
     * 튜립의 DomesticRegionCategory로부터 TourApiAreaCode를 찾는다
     *
     * @param category 튜립의 국내 지역 카테고리
     * @return 매핑되는 TourApiAreaCode, 없으면 NOT_FOUND
     */
    public static TourApiAreaCode fromDomesticRegionCategory(DomesticRegionCategory category) {
        return Arrays.stream(values())
                .filter(code -> code != NOT_FOUND)
                .filter(code -> code.domesticRegionCategory == category)
                .findFirst()
                .orElse(NOT_FOUND);
    }

    public boolean isFound() {
        return !(this == NOT_FOUND);
    }
}
