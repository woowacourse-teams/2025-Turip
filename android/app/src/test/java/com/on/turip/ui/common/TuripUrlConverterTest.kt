package com.on.turip.ui.common

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class TuripUrlConverterTest {
    @Test
    fun `유튜브 영상 url에서 videoId를 추출한다`() {
        // given:
        val youtubeUrl = "https://www.youtube.com/watch?v=PIVg1paYVX8"
        val expected = "PIVg1paYVX8"

        // when:
        val actual = TuripUrlConverter.extractVideoId(youtubeUrl)

        // then:
        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `유튜브 영상 url에서 유튜브 영상 썸네일 url로 변환한다`() {
        // given:
        val youtubeUrl = "https://www.youtube.com/watch?v=PIVg1paYVX8"
        val expected = "https://img.youtube.com/vi/PIVg1paYVX8/maxresdefault.jpg"

        // when:
        val actual = TuripUrlConverter.convertVideoThumbnailUrl(youtubeUrl)

        // then:
        assertThat(actual).isEqualTo(expected)
    }
}
