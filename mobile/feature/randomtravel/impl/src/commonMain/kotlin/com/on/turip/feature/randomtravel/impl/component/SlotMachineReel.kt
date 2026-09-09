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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

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
 *
 * 칸은 평면이 아니라 원통 표면에 붙어 있다고 보고 그린다. 중앙에서 [DEGREES_PER_SLOT]씩 떨어진
 * 각도로 매핑해 세로 위치는 `R·sin θ`, 기울기는 `-θ`, 크기·투명도는 `cos θ` 로 계산한다.
 * 그래서 가운데는 정면으로 크게, 끝으로 갈수록 눕고 촘촘해지며 드럼이 굴러가는 입체감이 생긴다.
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
    // 드럼 반지름은 창 높이의 절반. 정확히 ±90°인 칸이 창 위아래 끝에 닿으므로 드럼의
    // 앞면 절반이 창을 빈틈없이 채우고, 끝 칸은 완전히 눕는다.
    val reelRadiusPx: Float = itemHeightPx * VISIBLE_ITEM_COUNT / 2f

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(SLOT_ITEM_HEIGHT * VISIBLE_ITEM_COUNT)
                .clip(TuripTheme.shape.chip)
                .background(TuripTheme.colors.white)
                // 칸 경계마다 얇은 홈. 릴 자체는 움직여도 기계의 창살은 제자리에 있다.
                .drawWithContent {
                    for (line in 1 until VISIBLE_ITEM_COUNT) {
                        val y: Float = size.height * line / VISIBLE_ITEM_COUNT
                        drawLine(
                            color = SLOT_DIVIDER_COLOR,
                            start = Offset(x = 0f, y = y),
                            end = Offset(x = size.width, y = y),
                            strokeWidth = SLOT_DIVIDER_WIDTH.toPx(),
                        )
                    }
                    drawContent()
                }.clipToBounds(),
        contentAlignment = Alignment.Center,
    ) {
        for (delta in -DRAWN_ITEM_RANGE..DRAWN_ITEM_RANGE) {
            val slotPosition: Float = delta - fraction
            if (abs(slotPosition) > DRAWN_ITEM_RANGE) continue

            val index: Int = (baseIndex + delta).mod(displayNames.size)
            val distance: Float = abs(slotPosition)
            val angleDegrees: Float = slotPosition * DEGREES_PER_SLOT
            val angleCosine: Float = cos(angleDegrees * DEGREES_TO_RADIANS)

            // 뒤로 넘어간 칸은 그리지 않는다.
            if (angleCosine <= 0f) continue

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
                            cameraDistance = CAMERA_DISTANCE_MULTIPLIER * density
                            translationY = reelRadiusPx * sin(angleDegrees * DEGREES_TO_RADIANS)
                            rotationX = -angleDegrees
                            alpha =
                                (MIN_SLOT_ALPHA + (1f - MIN_SLOT_ALPHA) * angleCosine)
                                    .coerceIn(0f, 1f)
                            val scale: Float = MIN_SLOT_SCALE + (1f - MIN_SLOT_SCALE) * angleCosine
                            scaleX = scale
                            scaleY = scale
                        },
            )
        }

        // 드럼이 안쪽으로 말려 들어가며 생기는 위아래 음영.
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .background(CylinderShadeBrush),
        )
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

/**
 * 원통 곡면의 명암. 끝으로 갈수록 급격히 어두워지고 가운데 두 칸 구간은 거의 흰색으로 남는다.
 * 칸을 지우지 않고 어둡게만 덮기 때문에 시안처럼 맨 끝 지역명도 잘린 채 비쳐 보인다.
 */
private val CylinderShadeBrush: Brush =
    Brush.verticalGradient(
        0f to Color.Black.copy(alpha = 0.42f),
        0.08f to Color.Black.copy(alpha = 0.26f),
        0.20f to Color.Black.copy(alpha = 0.09f),
        0.36f to Color.Black.copy(alpha = 0.015f),
        0.5f to Color.Transparent,
        0.64f to Color.Black.copy(alpha = 0.015f),
        0.80f to Color.Black.copy(alpha = 0.09f),
        0.92f to Color.Black.copy(alpha = 0.26f),
        1f to Color.Black.copy(alpha = 0.42f),
    )

private val SLOT_DIVIDER_COLOR: Color = Color.Black.copy(alpha = 0.05f)
private val SLOT_DIVIDER_WIDTH: Dp = 1.dp

private val SLOT_ITEM_HEIGHT: Dp = 56.dp
private val MACHINE_WIDTH: Dp = 200.dp
private val MACHINE_PADDING: Dp = 8.dp
private val BORDER_WIDTH: Dp = 1.dp
private val BAND_BORDER_WIDTH: Dp = 1.5.dp
private val BAND_PIN_SIZE: Dp = 16.dp
private val MARKER_WIDTH: Dp = 8.dp
private val MARKER_HEIGHT: Dp = 12.dp

private const val VISIBLE_ITEM_COUNT: Int = 5
private const val DRAWN_ITEM_RANGE: Int = 4
private const val SPIN_DURATION_MILLIS: Int = 2400
private const val IDLE_STEP_DURATION_MILLIS: Int = 550
private const val CENTER_SLOT_THRESHOLD: Float = 0.5f

/**
 * 한 칸이 차지하는 원통 각도. 값이 클수록 적은 칸으로 90°에 도달해 곡률이 강해진다.
 * 24°면 중앙 이웃 칸 간격이 한 칸 높이와 거의 같아진다(`2.5 · sin 24° ≈ 1.02`).
 */
private const val DEGREES_PER_SLOT: Float = 24f
private const val DEGREES_TO_RADIANS: Float = (PI / 180.0).toFloat()

/** 원근 강도. 값이 작을수록 눕는 칸이 더 크게 왜곡된다. */
private const val CAMERA_DISTANCE_MULTIPLIER: Float = 7f
private const val MIN_SLOT_SCALE: Float = 0.82f

/** 끝 칸도 음영 아래로 비쳐 보여야 하므로 완전히 투명해지지는 않는다. */
private const val MIN_SLOT_ALPHA: Float = 0.55f

/** 빠르게 시작해 끝에서 길게 감속하는 커브 */
private val SpinEasing: Easing = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
