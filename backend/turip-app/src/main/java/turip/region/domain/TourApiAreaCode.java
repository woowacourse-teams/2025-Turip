package turip.region.domain;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 한국관광공사 TourAPI의 지역 코드(areaCode)와 시군구 코드(sigunguCode)를 관리하는 enum
 * 튜립 서버의 DomesticRegionCategory와 매핑하여 연관 관광지 정보 조회에 사용
 */
@Getter
@RequiredArgsConstructor
public enum TourApiAreaCode {

    /**
     * 서울특별시
     * areaCode: 1
     */
    SEOUL(1, null, DomesticRegionCategory.SEOUL),

    /**
     * 인천광역시
     * areaCode: 2
     */
    INCHEON(2, null, DomesticRegionCategory.INCHEON),

    /**
     * 대전광역시
     * areaCode: 3
     */
    DAEJEON(3, null, DomesticRegionCategory.DAEJEON),

    /**
     * 대구광역시
     * areaCode: 4
     */
    DAEGU(4, null, null),

    /**
     * 광주광역시
     * areaCode: 5
     */
    GWANGJU(5, null, null),

    /**
     * 부산광역시
     * areaCode: 6
     */
    BUSAN(6, null, DomesticRegionCategory.BUSAN),

    /**
     * 울산광역시
     * areaCode: 7
     */
    ULSAN(7, null, null),

    /**
     * 세종특별자치시
     * areaCode: 8
     */
    SEJONG(8, null, null),

    /**
     * 경기도
     * areaCode: 31
     */
    GYEONGGI(31, null, null),

    /**
     * 강원특별자치도
     * areaCode: 32
     */
    GANGWON(32, null, null),

    /**
     * 강원도 > 강릉시
     * areaCode: 32, sigunguCode: 1
     */
    GANGNEUNG(32, 1, DomesticRegionCategory.GANGNEUNG),

    /**
     * 강원도 > 속초시
     * areaCode: 32, sigunguCode: 3
     */
    SOKCHO(32, 3, DomesticRegionCategory.SOKCHO),

    /**
     * 충청북도
     * areaCode: 33
     */
    CHUNGBUK(33, null, null),

    /**
     * 충청남도
     * areaCode: 34
     */
    CHUNGNAM(34, null, null),

    /**
     * 경상북도
     * areaCode: 35
     */
    GYEONGBUK(35, null, null),

    /**
     * 경상북도 > 경주시
     * areaCode: 35, sigunguCode: 2
     */
    GYEONGJU(35, 2, DomesticRegionCategory.GYEONGJU),

    /**
     * 경상남도
     * areaCode: 36
     */
    GYEONGNAM(36, null, null),

    /**
     * 전북특별자치도
     * areaCode: 37
     */
    JEONBUK(37, null, null),

    /**
     * 전북특별자치도 > 전주시
     * areaCode: 37, sigunguCode: 2
     */
    JEONJU(37, 2, DomesticRegionCategory.JEONJU),

    /**
     * 전라남도
     * areaCode: 38
     */
    JEONNAM(38, null, null),

    /**
     * 제주특별자치도
     * areaCode: 39
     */
    JEJU(39, null, DomesticRegionCategory.JEJU),

    /**
     * 매핑되지 않는 지역 (OTHER_DOMESTIC 등)
     */
    NOT_FOUND(null, null, null);

    /**
     * 한국관광공사 TourAPI의 지역 코드 (시도 단위)
     */
    private final Integer areaCode;

    /**
     * 한국관광공사 TourAPI의 시군구 코드 (시군구 단위)
     * null인 경우 시도 전체를 의미
     */
    private final Integer sigunguCode;

    /**
     * 튜립 서버의 국내 지역 카테고리
     * null인 경우 현재 튜립에서 미지원하는 지역
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
}
