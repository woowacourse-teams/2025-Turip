package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.isActive
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * 슬롯 릴의 회전 단계.
 *
 * [Idle] 동안은 당첨 여행지를 모르는 채로 플레이스홀더로 무한 회전한다. 결과가 도착해 [Spinning]으로
 * 바뀌면 그 오프셋을 이어받아 당첨 칸에서 감속 정지한다 — 로딩과 회전이 끊기지 않고 하나로 이어진다.
 */
internal enum class SlotReelPhase {
    Idle,
    Spinning,
    Stopped,
}

/**
 * 여행지 이름이 위로 흘러가며 감속하는 슬롯 릴.
 *
 * 당첨 여행지는 [reelNames]의 마지막 칸이다. 전체 지역 목록을 그리지 않고 화면에 걸치는 칸만 그린다.
 */
@Composable
internal fun SlotMachineReel(
    reelNames: ImmutableList<String>,
    reelPhase: SlotReelPhase,
    reduceMotion: Boolean,
    onSpinFinished: () -> Unit,
    borderColor: Color,
    modifier: Modifier = Modifier,
) {
    val displayNames: ImmutableList<String> = reelNames.ifEmpty { IDLE_PLACEHOLDER_NAMES }
    val reelOffset: Animatable<Float, *> = remember { Animatable(0f) }
    val itemHeightPx: Float = with(LocalDensity.current) { SLOT_ITEM_HEIGHT.toPx() }
    val currentOnSpinFinished: () -> Unit by rememberUpdatedState(onSpinFinished)

    LaunchedEffect(reelPhase, reelNames, reduceMotion) {
        when (reelPhase) {
            SlotReelPhase.Idle -> {
                if (reduceMotion) return@LaunchedEffect
                while (isActive) {
                    reelOffset.animateTo(
                        targetValue = reelOffset.value + 1f,
                        animationSpec = tween(durationMillis = IDLE_STEP_DURATION_MILLIS, easing = LinearEasing),
                    )
                }
            }

            SlotReelPhase.Spinning -> {
                val targetOffset: Float =
                    nextStopOffset(
                        current = reelOffset.value,
                        reelSize = reelNames.size,
                        targetIndex = reelNames.lastIndex,
                        minDistance = if (reduceMotion) 0f else reelNames.size.toFloat(),
                    )

                if (reduceMotion) {
                    reelOffset.snapTo(targetOffset)
                } else {
                    reelOffset.animateTo(
                        targetValue = targetOffset,
                        animationSpec = tween(durationMillis = SPIN_DURATION_MILLIS, easing = SpinEasing),
                    )
                }
                currentOnSpinFinished()
            }

            SlotReelPhase.Stopped -> {
                if (reelNames.isEmpty()) return@LaunchedEffect
                reelOffset.snapTo(
                    nextStopOffset(
                        current = reelOffset.value,
                        reelSize = reelNames.size,
                        targetIndex = reelNames.lastIndex,
                        minDistance = 0f,
                    ),
                )
            }
        }
    }

    val currentOffset: Float = reelOffset.value
    val baseIndex: Int = floor(currentOffset).toInt()
    val fraction: Float = currentOffset - baseIndex

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(SLOT_ITEM_HEIGHT * VISIBLE_ITEM_COUNT)
                .clip(TuripTheme.shape.largeContainer)
                .background(TuripTheme.colors.container)
                .border(
                    width = BORDER_WIDTH,
                    color = borderColor,
                    shape = TuripTheme.shape.largeContainer,
                ).clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        for (delta in -DRAWN_ITEM_RANGE..DRAWN_ITEM_RANGE) {
            val slotPosition: Float = delta - fraction
            if (abs(slotPosition) > DRAWN_ITEM_RANGE) continue

            val index: Int = (baseIndex + delta).mod(displayNames.size)

            Text(
                text = displayNames[index],
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.gray05,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TuripTheme.spacing.large)
                        .graphicsLayer {
                            translationY = slotPosition * itemHeightPx
                            val distance: Float = abs(slotPosition)
                            alpha = (1f - distance * FADE_PER_SLOT).coerceIn(0f, 1f)
                            val scale: Float = (1f - distance * SHRINK_PER_SLOT).coerceIn(0f, 1f)
                            scaleX = scale
                            scaleY = scale
                        },
            )
        }
    }
}

/**
 * 이미 돌고 있던 [current] 오프셋을 이어받아, [reelSize]칸짜리 릴에서 [targetIndex]가 정중앙에 오는
 * 가장 가까운(단 [minDistance] 이상 더 진행하는) 오프셋을 구한다.
 */
private fun nextStopOffset(
    current: Float,
    reelSize: Int,
    targetIndex: Int,
    minDistance: Float,
): Float {
    val target: Int = targetIndex.mod(reelSize)
    var candidate: Int = ceil(current).toInt()
    candidate += (target - candidate).mod(reelSize)
    while (candidate - current < minDistance) candidate += reelSize
    return candidate.toFloat()
}

private val IDLE_PLACEHOLDER_NAMES: ImmutableList<String> =
    persistentListOf("서울", "부산", "제주", "인천", "대전", "전주", "강릉", "속초")

private val SLOT_ITEM_HEIGHT: Dp = 64.dp
private val BORDER_WIDTH: Dp = 1.5.dp
private const val VISIBLE_ITEM_COUNT: Int = 3
private const val DRAWN_ITEM_RANGE: Int = 2
private const val SPIN_DURATION_MILLIS: Int = 2400
private const val IDLE_STEP_DURATION_MILLIS: Int = 550
private const val FADE_PER_SLOT: Float = 0.55f
private const val SHRINK_PER_SLOT: Float = 0.15f

/** 빠르게 시작해 끝에서 길게 감속하는 커브 */
private val SpinEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
