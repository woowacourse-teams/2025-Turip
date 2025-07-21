package com.on.turip.domain.region

enum class Region(
    private val regionType: RegionType,
) {
    SEOUL(RegionType.METROPOLITAN_CITY),
    BUSAN(RegionType.METROPOLITAN_CITY),
    DAEGU(RegionType.METROPOLITAN_CITY),
    INCHEON(RegionType.METROPOLITAN_CITY),
    GWANGJU(RegionType.METROPOLITAN_CITY),
    DAEJEON(RegionType.METROPOLITAN_CITY),
    ULSAN(RegionType.METROPOLITAN_CITY),
    SEJONG(RegionType.METROPOLITAN_CITY),
    GYEONGGI(RegionType.PROVINCE),
    GANGWON(RegionType.PROVINCE),
    CHUNGCHEONGBUK(RegionType.PROVINCE),
    CHUNGCHEONGNAM(RegionType.PROVINCE),
    JEOLLABUK(RegionType.PROVINCE),
    JEOLLANAM(RegionType.PROVINCE),
    GYEONGSANGBUK(RegionType.PROVINCE),
    GYEONGSANGNAM(RegionType.PROVINCE),
    JEJU(RegionType.METROPOLITAN_CITY),
    ;

    companion object {
        fun from(regionType: RegionType): List<Region> = Region.entries.filter { it.regionType == regionType }
    }
}
