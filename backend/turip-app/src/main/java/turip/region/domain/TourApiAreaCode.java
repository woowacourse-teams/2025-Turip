package turip.region.domain;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 한국관광공사 TourAPI의 지역 코드(areaCode)와 시군구 코드(sigunguCode)를 관리하는 enum 튜립 서버의 DomesticRegionCategory와 매핑하여 연관 관광지 정보 조회에 사용
 */
@Getter
@RequiredArgsConstructor
public enum TourApiAreaCode {

    /**
     * 서울특별시 areaCode: 11 sigunguCodes: 종로구(11110), 마포구(11440), 중구(11140)
     */
    SEOUL(11, List.of(11110, 11440, 11140), DomesticRegionCategory.SEOUL, VisitorQueryLevel.PROVINCE),

    /**
     * 인천광역시 areaCode: 28 sigunguCodes: 중구(28110), 강화군(28710), 연수구(28185)
     */
    INCHEON(28, List.of(28110, 28710, 28185), DomesticRegionCategory.INCHEON, VisitorQueryLevel.PROVINCE),

    /**
     * 대전광역시 areaCode: 30 sigunguCodes: 유성구(30200), 중구(30140), 서구(30170)
     */
    DAEJEON(30, List.of(30200, 30140, 30170), DomesticRegionCategory.DAEJEON, VisitorQueryLevel.PROVINCE),

    /**
     * 부산광역시 areaCode: 26 sigunguCodes: 해운대구(26350), 중구(26110), 부산진구(26230)
     */
    BUSAN(26, List.of(26350, 26110, 26230), DomesticRegionCategory.BUSAN, VisitorQueryLevel.PROVINCE),

    /**
     * 강원도 > 강릉시 areaCode: 51 sigunguCodes: 강릉시(51150)
     */
    GANGNEUNG(51, List.of(51150), DomesticRegionCategory.GANGNEUNG, VisitorQueryLevel.CITY),

    /**
     * 강원도 > 속초시 areaCode: 51 sigunguCodes: 속초시(51210)
     */
    SOKCHO(51, List.of(51210), DomesticRegionCategory.SOKCHO, VisitorQueryLevel.CITY),

    /**
     * 경상북도 > 경주시 areaCode: 47 sigunguCodes: 경주시(47130)
     */
    GYEONGJU(47, List.of(47130), DomesticRegionCategory.GYEONGJU, VisitorQueryLevel.CITY),

    /**
     * 전북특별자치도 > 전주시 areaCode: 52 sigunguCodes: 전주시 완산구(52111), 전주시 덕진구(52113)
     */
    JEONJU(52, List.of(52111, 52113), DomesticRegionCategory.JEONJU, VisitorQueryLevel.CITY),

    /**
     * 제주특별자치도 areaCode: 50 sigunguCodes: 제주시(50110), 서귀포시(50130)
     */
    JEJU(50, List.of(50110, 50130), DomesticRegionCategory.JEJU, VisitorQueryLevel.PROVINCE),

    /**
     * 충청남도 > 공주시 areaCode: 44 sigunguCodes: 공주시(44150)
     */
    GONGJU(44, List.of(44150), DomesticRegionCategory.GONGJU, VisitorQueryLevel.CITY),

    /**
     * 전라남도 > 여수시 areaCode: 46 sigunguCodes: 여수시(46130)
     */
    YEOSU(46, List.of(46130), DomesticRegionCategory.YEOSU, VisitorQueryLevel.CITY),

    /**
     * 경기도 > 수원시 areaCode: 41 sigunguCodes: 장안구(41111), 팔달구(41115), 영통구(41117)
     */
    SUWON(41, List.of(41111, 41115, 41117), DomesticRegionCategory.SUWON, VisitorQueryLevel.CITY),

    /**
     * 전북특별자치도 > 군산시 areaCode: 52 sigunguCodes: 군산시(52130)
     */
    GUNSAN(52, List.of(52130), DomesticRegionCategory.GUNSAN, VisitorQueryLevel.CITY),

    /**
     * 대구광역시 areaCode: 27 sigunguCodes: 중구(27110), 수성구(27260), 달서구(27290)
     */
    DAEGU(27, List.of(27110, 27260, 27290), DomesticRegionCategory.DAEGU, VisitorQueryLevel.PROVINCE),

    /**
     * 매핑되지 않는 지역 (OTHER_DOMESTIC 등)
     */
    NOT_FOUND(null, Collections.emptyList(), null, null);

    /**
     * 한국관광공사 TourAPI의 지역 코드 (시도 단위)
     */
    private final Integer areaCode;

    /**
     * 한국관광공사 TourAPI의 시군구 코드 목록 (최대 3개) 빈 리스트인 경우 시도 전체를 의미
     */
    private final List<Integer> sigunguCodes;

    /**
     * 튜립 서버의 국내 지역 카테고리 null인 경우 현재 튜립에서 미지원하는 지역
     */
    private final DomesticRegionCategory domesticRegionCategory;

    /**
     * 데이터랩 방문자 수 API 집계 단위 (PROVINCE: 시도 전체, CITY: 시군구) null인 경우 미지원 지역
     */
    private final VisitorQueryLevel visitorQueryLevel;

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

    /**
     * 지정한 집계 단위(PROVINCE/CITY)에 해당하는 지원 지역 목록을 반환한다
     *
     * @param level 방문자 수 API 집계 단위
     * @return 해당 집계 단위의 TourApiAreaCode 목록 (NOT_FOUND 제외)
     */
    public static List<TourApiAreaCode> findByVisitorQueryLevel(VisitorQueryLevel level) {
        return Arrays.stream(values())
                .filter(TourApiAreaCode::isFound)
                .filter(code -> code.visitorQueryLevel == level)
                .toList();
    }

    public boolean isFound() {
        return !(this == NOT_FOUND);
    }
}
