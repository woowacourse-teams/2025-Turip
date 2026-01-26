package com.on.turip.ui.compose.favorite.util.reorderable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.on.turip.ui.compose.favorite.util.reorderable.Scroller.Direction.BACKWARD
import com.on.turip.ui.compose.favorite.util.reorderable.Scroller.Direction.FORWARD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 *
 */
@Composable
fun rememberScroller(
    scrollableState: ScrollableState,
    scrollDistanceProvider: () -> Float,
    duration: Long = 100L,
): Scroller {
    val scope = rememberCoroutineScope()
    val scrollDistanceProviderUpdated = rememberUpdatedState(scrollDistanceProvider)
    val durationUpdated = rememberUpdatedState(duration)

    return remember(scrollableState, scope, duration) {
        Scroller(
            scrollableState,
            scope,
            scrollSpeedPxPerSecondProvider = { scrollDistanceProviderUpdated.value() / (durationUpdated.value / 1_000f) },
        )
    }
}

data class ScrollInfo(
    val direction: Scroller.Direction,
    val speedMultiplier: Float,
    val maxScrollDistanceProvider: () -> Float,
    val onScroll: suspend () -> Unit,
) {
    companion object {
        val NONE = ScrollInfo(Scroller.Direction.FORWARD, 0f, { 0f }, {})
    }
}

@Stable
class Scroller(
    private val scrollableState: ScrollableState,
    private val scope: CoroutineScope,
    private val scrollSpeedPxPerSecondProvider: () -> Float,
) {
    private var programmaticScrollJob: Job? = null
    val isScrolling: Boolean
        get() = programmaticScrollJob?.isActive == true
    private val scrollInfoChannel = Channel<ScrollInfo>(Channel.CONFLATED)

    fun start(
        direction: Direction,
        speedMultiplier: Float,
        maxScrollDistanceProvider: () -> Float,
        onScroll: suspend () -> Unit,
    ): Boolean {
        if (!canScroll(direction)) return false

        if (programmaticScrollJob == null) {
            programmaticScrollJob = scope.launch { scrollLoop() }
        }

        val scrollInfo = ScrollInfo(direction, speedMultiplier, maxScrollDistanceProvider, onScroll)

        scrollInfoChannel.trySend(scrollInfo)
        return true
    }

    private suspend fun scrollLoop() {
        var scrollInfo: ScrollInfo? = null

        while (true) {
            scrollInfo = scrollInfoChannel.tryReceive().getOrNull() ?: scrollInfo
            if (scrollInfo == null || scrollInfo == ScrollInfo.NONE) break

            val (direction, speedMultiplier, maxScrollDistanceProvider, onScroll) = scrollInfo

            val pixelPerSecond = scrollSpeedPxPerSecondProvider() * speedMultiplier
            val pixelPerMs = pixelPerSecond / 1_000f

            onScroll()

            if (!canScroll(direction)) break

            val maxScrollDistance = maxScrollDistanceProvider()
            // 아직 스크롤 가능한 거리가 아니라면 delay 후 loop 다시 진행
            if (maxScrollDistance <= 0f) {
                delay(ZERO_SCROLL_WAIT_DURATION)
                continue
            }
            val maxScrollDistanceDuration = maxScrollDistance / pixelPerMs
            val duration = maxScrollDistanceDuration.toLong().coerceIn(1L, MAX_SCROLL_DURATION)
            val scrollDistance = maxScrollDistance * (duration / maxScrollDistanceDuration)
            val diff =
                scrollDistance.let {
                    when (direction) {
                        BACKWARD -> -it
                        FORWARD -> it
                    }
                }

            // tween : 시간 기반 animation
            scrollableState.animateScrollBy(
                value = diff,
                animationSpec = tween(durationMillis = duration.toInt(), easing = LinearEasing),
            )
        }
    }

    // 스크롤 더 할 수 있는지 판단
    private fun canScroll(direction: Direction): Boolean =
        when (direction) {
            FORWARD -> scrollableState.canScrollForward
            BACKWARD -> scrollableState.canScrollBackward
        }

    // 스크롤 종료 & 리소스 정리
    fun tryStop() {
        scope.launch {
            scrollInfoChannel.send(ScrollInfo.NONE)
            programmaticScrollJob?.cancelAndJoin()
            programmaticScrollJob = null
        }
    }

    companion object {
        private const val MAX_SCROLL_DURATION = 100L
        private const val ZERO_SCROLL_WAIT_DURATION = 1_000L
    }

    /**
     * ScrollableState 기준의 스크롤 방향
     */
    enum class Direction {
        /** 콘텐츠 시작 방향 (LazyColumn 위쪽 방향) */
        BACKWARD,

        /** 콘텐츠 끝 방향 (LazyColumn 아래쪽 방향) */
        FORWARD,
        ;

        val opposite: Direction
            get() =
                when (this) {
                    BACKWARD -> FORWARD
                    FORWARD -> BACKWARD
                }
    }
}
