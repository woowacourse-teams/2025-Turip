package com.on.turip.feature.popularregion.impl.map

/**
 * 법정동 시도 코드로 지도 위 자리를 찾는 표.
 *
 * 방문자 수 API는 좌표를 주지 않으므로 지도에 찍을 위치는 클라이언트가 갖고 있어야 한다.
 * 시도 경계와 코드 체계는 바뀌는 일이 드물어 상수로 둔다.
 *
 * 이름도 함께 갖는다. 서버가 주는 이름은 `강원특별자치도` 처럼 정식 명칭이라
 * 지도 라벨로 쓰기엔 길기 때문이다. 서버 이름은 시트 제목에서 그대로 쓴다.
 *
 * 표에 없는 코드가 내려오면 지도에 놓을 자리를 모르므로 화면에서 제외한다.
 * 행정구역이 개편되면 서버가 새 코드를 먼저 내려주므로, 빠진 코드는 로그로 드러나게 두고 여기에 한 줄을 더한다.
 */
internal object SidoAreas {
    /**
     * @param shortName 지도 라벨과 시트에 쓰는 짧은 이름
     * @param location 시도청 소재지 좌표
     */
    data class SidoArea(
        val shortName: String,
        val location: GeoPoint,
    )

    private val AREAS: Map<Int, SidoArea> =
        mapOf(
            11 to SidoArea("서울", GeoPoint(37.5665, 126.9780)),
            // 전남·광주 통합시. 두 지역을 합친 영역의 중심이라 전남(46)·광주(29)와 자리가 겹친다.
            12 to SidoArea("전남광주", GeoPoint(34.9500, 126.9000)),
            26 to SidoArea("부산", GeoPoint(35.1796, 129.0756)),
            27 to SidoArea("대구", GeoPoint(35.8714, 128.6014)),
            28 to SidoArea("인천", GeoPoint(37.4563, 126.7052)),
            29 to SidoArea("광주", GeoPoint(35.1595, 126.8526)),
            30 to SidoArea("대전", GeoPoint(36.3504, 127.3845)),
            31 to SidoArea("울산", GeoPoint(35.5384, 129.3114)),
            36 to SidoArea("세종", GeoPoint(36.4801, 127.2890)),
            41 to SidoArea("경기", GeoPoint(37.4138, 127.5183)),
            43 to SidoArea("충북", GeoPoint(36.8000, 127.7000)),
            44 to SidoArea("충남", GeoPoint(36.5184, 126.8000)),
            46 to SidoArea("전남", GeoPoint(34.8679, 126.9910)),
            47 to SidoArea("경북", GeoPoint(36.4919, 128.8889)),
            48 to SidoArea("경남", GeoPoint(35.4606, 128.2132)),
            50 to SidoArea("제주", GeoPoint(33.4996, 126.5312)),
            51 to SidoArea("강원", GeoPoint(37.8228, 128.1555)),
            52 to SidoArea("전북", GeoPoint(35.7175, 127.1530)),
        )

    /**
     * 튜립의 지역 카테고리가 속한 시도.
     *
     * 카테고리는 `강릉`, `속초` 처럼 시군구 이름이라 시도 코드로 직접 환산되지 않는다.
     * 한 시도에 카테고리가 여럿 붙을 수 있다(강원 → 강릉·속초).
     *
     * 어떤 카테고리가 실제로 존재하는지는 서버가 정한다. 이 표는 이름을 코드로 바꿔줄 뿐이라
     * 서버 목록에 없는 이름은 조회되지 않고, 서버에 새 지역이 생기면 여기에 한 줄을 더한다.
     */
    private val AREA_CODES_BY_REGION_CATEGORY: Map<String, Int> =
        mapOf(
            "서울" to 11,
            "부산" to 26,
            "인천" to 28,
            "대전" to 30,
            "경주" to 47,
            "제주" to 50,
            "강릉" to 51,
            "속초" to 51,
            "전주" to 52,
        )

    /**
     * 통합 시도와, 그 통합시에 흡수된 옛 시도.
     *
     * 서버가 통합시(12 전남광주)와 구성 지역(46 전남, 29 광주)을 아직 함께 내려준다.
     * 그대로 그리면 같은 땅이 세 조각으로 나뉘고 방문자 수도 중복 집계돼 서남권이 실제보다 뜨겁게 보인다.
     * 통합시 쪽을 남기고 구성 지역을 접는 임시 대응이며, 서버가 구성 지역을 빼고 내려주면 이 표만 지우면 된다.
     */
    private val SUPERSEDED_AREA_CODES: Map<Int, Set<Int>> =
        mapOf(
            12 to setOf(46, 29),
        )

    fun areaOf(areaCode: Int): SidoArea? = AREAS[areaCode]

    /**
     * [presentAreaCodes] 에 통합시가 있을 때, 거기에 흡수돼 지도에서 접어야 할 코드들.
     *
     * 통합시가 내려오지 않으면 빈 집합이라 옛 시도가 그대로 그려진다.
     * 서버가 어느 쪽을 주든 지도가 겹치지 않게 하려는 것이다.
     */
    fun supersededAreaCodes(presentAreaCodes: Set<Int>): Set<Int> =
        SUPERSEDED_AREA_CODES
            .filterKeys { it in presentAreaCodes }
            .values
            .flatten()
            .toSet()

    fun areaCodeOf(regionCategoryName: String): Int? = AREA_CODES_BY_REGION_CATEGORY[regionCategoryName]
}
