package turip.region.domain;

/**
 * 한국관광 데이터랩 방문자 수 API의 집계 단위
 */
public enum VisitorQueryLevel {

    /**
     * 광역지자체(시도) 단위 - metcoRegnVisitrDDList, areaCode 기준 집계 예: 서울특별시 전체, 부산광역시 전체
     */
    PROVINCE,

    /**
     * 기초지자체(시군구) 단위 - locgoRegnVisitrDDList, signguCode 기준 집계 예: 강릉시, 수원 팔달구
     */
    CITY
}
