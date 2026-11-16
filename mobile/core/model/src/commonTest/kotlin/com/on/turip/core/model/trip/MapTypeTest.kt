package com.on.turip.core.model.trip

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MapTypeTest {
    @Test
    fun `허용된 지도 host의 https 링크는 통과한다`() {
        assertTrue(MapType.isAllowedMapUrl("https://map.kakao.com/link/map/123"))
        assertTrue(MapType.isAllowedMapUrl("https://place.map.kakao.com/123"))
        assertTrue(MapType.isAllowedMapUrl("https://www.google.com/maps/place/x"))
        assertTrue(MapType.isAllowedMapUrl("https://maps.google.com:443/?q=x"))
        assertTrue(MapType.isAllowedMapUrl("HTTPS://MAP.KAKAO.COM/"))
    }

    @Test
    fun `https가 아닌 스킴은 거부한다`() {
        assertFalse(MapType.isAllowedMapUrl("http://map.kakao.com/"))
        assertFalse(MapType.isAllowedMapUrl("intent://map.kakao.com/#Intent;end"))
        assertFalse(MapType.isAllowedMapUrl("tel:01012345678"))
        assertFalse(MapType.isAllowedMapUrl(""))
    }

    @Test
    fun `host가 허용 도메인이 아니면 거부한다`() {
        assertFalse(MapType.isAllowedMapUrl("https://evil.com/?x=kakao.com"))
        assertFalse(MapType.isAllowedMapUrl("https://kakao.com.evil.com/"))
        assertFalse(MapType.isAllowedMapUrl("https://notkakao.com/"))
        assertFalse(MapType.isAllowedMapUrl("https://kakao.com@evil.com/"))
        assertFalse(MapType.isAllowedMapUrl("https://naver.me/abc"))
    }
}
