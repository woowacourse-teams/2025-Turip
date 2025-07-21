package com.on.turip.ui.common.model

import com.on.turip.domain.region.Region

enum class RegionModel(
    val korean: String,
    val english: String,
    private val origin: Region,
) {
    SEOUL("서울", "seoul", Region.SEOUL),
    BUSAN("부산", "busan", Region.BUSAN),
    DAEGU("대구", "daegu", Region.DAEGU),
    INCHEON("인천", "incheon", Region.INCHEON),
    GWANGJU("광주", "gwangju", Region.GWANGJU),
    DAEJEON("대전", "daejeon", Region.DAEJEON),
    ULSAN("울산", "ulsan", Region.ULSAN),
    SEJONG("세종", "sejong", Region.SEJONG),
    GYEONGGI("경기", "gyeonggi", Region.GYEONGGI),
    GANGWON("강원", "gangwon", Region.GANGWON),
    CHUNGCHEONGBUK("충북", "chungcheongbuk", Region.CHUNGCHEONGBUK),
    CHUNGCHEONGNAM("충남", "chungcheongnam", Region.CHUNGCHEONGNAM),
    JEOLLABUK("전북", "jeollabuk", Region.JEOLLABUK),
    JEOLLANAM("전남", "jeollanam", Region.JEOLLANAM),
    GYEONGSANGBUK("경북", "gyeongsangbuk", Region.GYEONGSANGBUK),
    GYEONGSANGNAM("경남", "gyeongsangnam", Region.GYEONGSANGNAM),
    JEJU("제주", "jeju", Region.JEJU),
    ;

    companion object {
        fun find(region: Region): RegionModel =
            RegionModel.entries.find { it.origin == region }
                ?: throw IllegalArgumentException("해당 $region 에 일치하는 Model이 없습니다.")
    }
}
