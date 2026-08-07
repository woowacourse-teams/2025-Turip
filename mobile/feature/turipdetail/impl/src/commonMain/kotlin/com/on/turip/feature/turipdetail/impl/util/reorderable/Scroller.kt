package com.on.turip.feature.turipdetail.impl.util.reorderable

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import com.on.turip.feature.turipdetail.impl.util.reorderable.Scroller.Direction.BACKWARD
import com.on.turip.feature.turipdetail.impl.util.reorderable.Scroller.Direction.FORWARD
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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
        val NONE = ScrollInfo(FORWARD, 0f, { 0f }, {})
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

        scrollInfoChannel.trySend(
            ScrollInfo(
                direction = direction,
                speedMultiplier = speedMultiplier,
                maxScrollDistanceProvider = maxScrollDistanceProvider,
                onScroll = onScroll,
            ),
        )
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

            scrollableState.animateScrollBy(
                value = diff,
                animationSpec = tween(durationMillis = duration.toInt(), easing = LinearEasing),
            )
        }
    }

    private fun canScroll(direction: Direction): Boolean =
        when (direction) {
            FORWARD -> scrollableState.canScrollForward
            BACKWARD -> scrollableState.canScrollBackward
        }

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

    enum class Direction {
        BACKWARD,
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
