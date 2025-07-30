package com.on.turip.ui.common

object TuripUrlConverter {
    private const val YOUTUBE_VIDEO_THUMBNAIL_FORM = "https://img.youtube.com/vi/%s/0.jpg"
    private const val VIDEO_ID_POSITION = 1

    private object VideoRegex {
        val patternVParam = "v=([a-zA-Z0-9_-]{11})(?:&|$)".toRegex()
        val patternShortUrl = "youtu\\.be/([a-zA-Z0-9_-]{11})".toRegex()
    }

    fun extractVideoId(videoUrl: String): String {
        val matches =
            VideoRegex.patternVParam.find(videoUrl) ?: VideoRegex.patternShortUrl.find(videoUrl)
        return matches?.groups?.get(VIDEO_ID_POSITION)?.value ?: ""
    }

    fun toVideoThumbnailUrl(videoUrl: String): String {
        val videoId = extractVideoId(videoUrl)
        return YOUTUBE_VIDEO_THUMBNAIL_FORM.format(videoId)
    }
}
