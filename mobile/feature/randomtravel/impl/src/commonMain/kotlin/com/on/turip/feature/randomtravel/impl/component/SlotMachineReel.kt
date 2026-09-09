package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.Path
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
 * 여행지 이름이 세로로 흘러가며 감속하는 단일 릴.
 *
 * 릴은 하나뿐이고 가운데 한 줄이 선택 영역이다. 화면 구성은 세 겹으로 분리돼 있다.
 * - 기계 외형([SlotMachineReel] 바깥 테두리·좌우 마커): 정적. 애니메이션하지 않는다.
 * - 지역 릴([ReelWindow]): 세로 이동만 담당한다.
 * - 티켓 이동 영역: 이 컴포저블 밖의 [TicketDispenser] 가 담당한다.
 *
 * 당첨 여행지는 [reelNames]의 마지막 칸이다. 전체 지역 목록을 그리지 않고 화면에 걸치는 칸만 그린다.
 */
@Composable
internal fun SlotMachineReel(
    reelNames: ImmutableList<String>,
    reelPhase: SlotReelPhase,
    reduceMotion: Boolean,
    onSpinFinished: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        SelectionMarker(isPointingRight = true)

        Box(
            modifier =
                Modifier
                    .padding(horizontal = TuripTheme.spacing.medium)
                    .width(MACHINE_WIDTH)
                    .clip(TuripTheme.shape.largeContainer)
                    .background(TuripTheme.colors.container)
                    .border(
                        width = BORDER_WIDTH,
                        color = TuripTheme.colors.border,
                        shape = TuripTheme.shape.largeContainer,
                    ).padding(MACHINE_PADDING),
            contentAlignment = Alignment.Center,
        ) {
            ReelWindow(
                reelNames = reelNames,
                reelPhase = reelPhase,
                reduceMotion = reduceMotion,
                onSpinFinished = onSpinFinished,
            )

            SelectionBand(isConfirmed = reelPhase == SlotReelPhase.Stopped)
        }

        SelectionMarker(isPointingRight = false)
    }
}

/**
 * 지역명이 위아래로 움직이는 영역. 이 안에서만 릴이 움직이고, 위아래 끝은 기계 안쪽으로
 * 말려 들어가듯 서서히 사라진다.
 */
@Composable
private fun ReelWindow(
    reelNames: ImmutableList<String>,
    reelPhase: SlotReelPhase,
    reduceMotion: Boolean,
    onSpinFinished: () -> Unit,
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
                .clip(TuripTheme.shape.chip)
                .background(TuripTheme.colors.white)
                // 위아래를 잘라내지 않고 흐리게 지워야 릴이 기계 안으로 이어지는 것처럼 보인다.
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(brush = FadeMaskBrush, blendMode = BlendMode.DstIn)
                }.clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        for (delta in -DRAWN_ITEM_RANGE..DRAWN_ITEM_RANGE) {
            val slotPosition: Float = delta - fraction
            if (abs(slotPosition) > DRAWN_ITEM_RANGE) continue

            val index: Int = (baseIndex + delta).mod(displayNames.size)
            val distance: Float = abs(slotPosition)

            Text(
                text = displayNames[index],
                style = TuripTheme.typography.title1,
                color =
                    if (distance < CENTER_SLOT_THRESHOLD) {
                        TuripTheme.colors.gray05
                    } else {
                        TuripTheme.colors.gray03
                    },
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = TuripTheme.spacing.medium)
                        .graphicsLayer {
                            translationY = slotPosition * itemHeightPx
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
 * 가운데 한 줄이 선택 영역이라는 표시. 릴 위에 얹히지만 릴과 함께 움직이지 않는 정적 크롬이다.
 */
@Composable
private fun SelectionBand(
    isConfirmed: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(SLOT_ITEM_HEIGHT)
                .clip(TuripTheme.shape.chip)
                .background(
                    if (isConfirmed) TuripTheme.colors.primarySub else Color.Transparent,
                ).border(
                    width = BAND_BORDER_WIDTH,
                    color = TuripTheme.colors.primary,
                    shape = TuripTheme.shape.chip,
                ),
        contentAlignment = Alignment.CenterStart,
    ) {
        if (isConfirmed) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
                modifier =
                    Modifier
                        .padding(start = TuripTheme.spacing.medium)
                        .size(BAND_PIN_SIZE),
            )
        }
    }
}

/** 선택 영역을 가리키는 좌우 삼각 마커. */
@Composable
private fun SelectionMarker(
    isPointingRight: Boolean,
    modifier: Modifier = Modifier,
) {
    val markerColor: Color = TuripTheme.colors.primary

    Canvas(
        modifier = modifier.size(width = MARKER_WIDTH, height = MARKER_HEIGHT),
    ) {
        val path: Path =
            Path().apply {
                if (isPointingRight) {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height / 2f)
                    lineTo(0f, size.height)
                } else {
                    moveTo(size.width, 0f)
                    lineTo(0f, size.height / 2f)
                    lineTo(size.width, size.height)
                }
                close()
            }
        drawPath(path = path, color = markerColor)
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

/** 위아래 끝을 서서히 지우는 마스크. DstIn 으로 그려 릴의 알파만 깎는다. */
private val FadeMaskBrush: Brush =
    Brush.verticalGradient(
        0f to Color.Transparent,
        0.22f to Color.Black,
        0.78f to Color.Black,
        1f to Color.Transparent,
    )

private val SLOT_ITEM_HEIGHT: Dp = 44.dp
private val MACHINE_WIDTH: Dp = 200.dp
private val MACHINE_PADDING: Dp = 8.dp
private val BORDER_WIDTH: Dp = 1.dp
private val BAND_BORDER_WIDTH: Dp = 1.5.dp
private val BAND_PIN_SIZE: Dp = 16.dp
private val MARKER_WIDTH: Dp = 8.dp
private val MARKER_HEIGHT: Dp = 12.dp

private const val VISIBLE_ITEM_COUNT: Int = 5
private const val DRAWN_ITEM_RANGE: Int = 3
private const val SPIN_DURATION_MILLIS: Int = 2400
private const val IDLE_STEP_DURATION_MILLIS: Int = 550
private const val FADE_PER_SLOT: Float = 0.3f
private const val SHRINK_PER_SLOT: Float = 0.06f
private const val CENTER_SLOT_THRESHOLD: Float = 0.5f

/** 빠르게 시작해 끝에서 길게 감속하는 커브 */
private val SpinEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
