package com.on.turip.domain.region

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class RegionTest {
    @Test
    fun `지역 타입이 광역시인 지역을 반환한다`() {
        // given:
        val expected: List<Region> =
            listOf(
                Region.SEOUL,
                Region.BUSAN,
                Region.DAEGU,
                Region.INCHEON,
                Region.GWANGJU,
                Region.DAEJEON,
                Region.ULSAN,
                Region.SEJONG,
                Region.JEJU,
            )

        // when:
        val result = Region.from(RegionType.METROPOLITAN_CITY)

        // then:
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `지역 타입이 도인 지역을 반환한다`() {
        // given:
        val expected: List<Region> =
            listOf(
                Region.GYEONGGI,
                Region.GANGWON,
                Region.CHUNGCHEONGBUK,
                Region.CHUNGCHEONGNAM,
                Region.JEOLLABUK,
                Region.JEOLLANAM,
                Region.GYEONGSANGBUK,
                Region.GYEONGSANGNAM,
            )

        // when:
        val result = Region.from(RegionType.PROVINCE)

        // then:
        assertThat(result).isEqualTo(expected)
    }
}
