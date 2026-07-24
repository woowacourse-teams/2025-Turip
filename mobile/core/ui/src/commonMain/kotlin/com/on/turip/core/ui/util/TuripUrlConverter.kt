package com.on.turip.core.ui.util

object TuripUrlConverter {
    private const val VIDEO_ID_POSITION: Int = 1

    private object VideoRegex {
        val patternVideoPath: Regex = "v=([a-zA-Z0-9_-]{11})(?:&|$)".toRegex()
        val patternShortUrl: Regex = "youtu\\.be/([a-zA-Z0-9_-]{11})".toRegex()
    }

    fun extractVideoId(videoUrl: String): String {
        val matches: MatchResult? =
            VideoRegex.patternVideoPath.find(videoUrl)
                ?: VideoRegex.patternShortUrl.find(videoUrl)
        return matches?.groups?.get(VIDEO_ID_POSITION)?.value ?: ""
    }

    fun convertVideoThumbnailUrl(videoUrl: String): String {
        val videoId: String = extractVideoId(videoUrl)
        return "https://img.youtube.com/vi/$videoId/0.jpg"
    }
}
