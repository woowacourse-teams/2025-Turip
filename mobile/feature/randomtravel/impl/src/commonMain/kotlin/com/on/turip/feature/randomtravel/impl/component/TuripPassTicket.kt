package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_abroad
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_code
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_destination_label
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_domestic
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_region_label
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_serial
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_title
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_video_count
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_video_label
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.feature.randomtravel.impl.model.RandomDestinationModel
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * 프린터에서 종이가 나오듯 위에서부터 조금씩 드러나는 티켓.
 *
 * 티켓 자체는 항상 온전한 크기로 측정하고, 배치 높이만 늘려가며 잘라 보여준다.
 * 슬라이드가 아니라 "출력"으로 읽히는 이유가 이 부분이다.
 */
@Composable
internal fun PrintedTicket(
    destination: RandomDestinationModel,
    reduceMotion: Boolean,
    modifier: Modifier = Modifier,
) {
    val printProgress: Animatable<Float, *> = remember(destination) { Animatable(0f) }

    LaunchedEffect(destination, reduceMotion) {
        if (reduceMotion) {
            printProgress.snapTo(1f)
            return@LaunchedEffect
        }
        printProgress.snapTo(0f)
        printProgress.animateTo(
            targetValue = 1f,
            animationSpec =
                tween(durationMillis = PRINT_DURATION_MILLIS, easing = LinearOutSlowInEasing),
        )
    }

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                // 출력 중에만 잘라낸다. 다 나온 뒤에도 클립을 유지하면 티켓 그림자까지 잘려나간다.
                .graphicsLayer { clip = printProgress.value < 1f },
    ) {
        TuripPassTicket(
            destination = destination,
            elevation = TICKET_ELEVATION * printProgress.value,
            modifier =
                Modifier.layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    val printedHeight: Int =
                        (placeable.height * printProgress.value).roundToInt().coerceAtLeast(0)
                    layout(placeable.width, printedHeight) { placeable.place(0, 0) }
                },
        )
    }
}

/**
 * 발급된 여행 티켓.
 *
 * Phase 1에서 채울 수 없는 항목(카테고리 · 이동시간 · 날씨)은 빈칸으로 두지 않고 아예 그리지 않는다.
 * 항목이 늘어나도 레이아웃이 깨지지 않도록 정보 칸을 [TicketInfoColumn]으로 반복 배치한다.
 */
@Composable
internal fun TuripPassTicket(
    destination: RandomDestinationModel,
    modifier: Modifier = Modifier,
    elevation: Dp = TICKET_ELEVATION,
) {
    val ticketShape: Shape =
        remember {
            TicketShape(
                cornerRadius = TICKET_CORNER_RADIUS,
                notchRadius = NOTCH_RADIUS,
                notchCenterY = TICKET_IMAGE_HEIGHT,
            )
        }

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .shadow(elevation = elevation, shape = ticketShape, clip = false)
                .background(color = TuripTheme.colors.white, shape = ticketShape)
                .border(width = 1.dp, color = TuripTheme.colors.cardBorder, shape = ticketShape)
                .clip(ticketShape),
    ) {
        TicketHeader(destination = destination)

        TicketPerforation()

        Column(
            modifier =
                Modifier.padding(
                    start = TuripTheme.spacing.extraLarge,
                    end = TuripTheme.spacing.extraLarge,
                    top = TuripTheme.spacing.large,
                    bottom = TuripTheme.spacing.extraLarge,
                ),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
            ) {
                TicketInfoColumn(
                    label = stringResource(Res.string.random_travel_ticket_region_label),
                    value =
                        if (destination.isDomestic) {
                            stringResource(Res.string.random_travel_ticket_domestic)
                        } else {
                            stringResource(Res.string.random_travel_ticket_abroad)
                        },
                    modifier = Modifier.weight(1f),
                )

                TicketInfoColumn(
                    label = stringResource(Res.string.random_travel_ticket_video_label),
                    value =
                        stringResource(Res.string.random_travel_ticket_video_count)
                            .formatResource(destination.videoCount),
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Text(
                text =
                    stringResource(Res.string.random_travel_ticket_code)
                        .formatResource(destination.name),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray02,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(top = TuripTheme.spacing.small),
            )
        }
    }
}

/**
 * 여행지 사진 위에 스크림을 깔고 목적지를 얹은 티켓 상단.
 */
@Composable
private fun TicketHeader(
    destination: RandomDestinationModel,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(TICKET_IMAGE_HEIGHT)
                .background(TuripTheme.colors.primarySub),
    ) {
        AsyncImage(
            model = destination.imageUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        brush =
                            Brush.verticalGradient(
                                colors =
                                    listOf(
                                        Color.Black.copy(alpha = SCRIM_TOP_ALPHA),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = SCRIM_BOTTOM_ALPHA),
                                    ),
                            ),
                    ),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopStart)
                    .padding(TuripTheme.spacing.large),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.random_travel_ticket_title),
                style = TuripTheme.typography.info2,
                color = Color.White,
            )

            Text(
                text =
                    stringResource(Res.string.random_travel_ticket_serial)
                        .formatResource(destination.serialNumber()),
                style = TuripTheme.typography.info2,
                color = Color.White.copy(alpha = SUBTLE_TEXT_ALPHA),
            )
        }

        Column(
            modifier =
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(
                        start = TuripTheme.spacing.extraLarge,
                        end = TuripTheme.spacing.extraLarge,
                        bottom = TuripTheme.spacing.large,
                    ),
        ) {
            Text(
                text = stringResource(Res.string.random_travel_ticket_destination_label),
                style = TuripTheme.typography.info2,
                color = Color.White.copy(alpha = SUBTLE_TEXT_ALPHA),
            )

            Text(
                text = destination.name,
                style = TuripTheme.typography.display,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun TicketInfoColumn(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray02,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = value,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray05,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = TuripTheme.spacing.extraSmall),
        )
    }
}

/** 절취선. 좌우 노치와 같은 높이에 그려져 한 줄로 이어져 보인다. */
@Composable
private fun TicketPerforation(modifier: Modifier = Modifier) {
    val lineColor: Color = TuripTheme.colors.gray02

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(PERFORATION_STROKE)
                .padding(horizontal = NOTCH_RADIUS)
                .drawBehind {
                    drawLine(
                        color = lineColor,
                        start = Offset(0f, size.height / 2),
                        end = Offset(size.width, size.height / 2),
                        strokeWidth = size.height,
                        pathEffect =
                            PathEffect.dashPathEffect(
                                intervals = floatArrayOf(DASH_LENGTH, DASH_GAP),
                            ),
                    )
                },
    )
}

/**
 * 좌우에 노치(반원 홈)가 파인 티켓 외곽선.
 */
private data class TicketShape(
    private val cornerRadius: Dp,
    private val notchRadius: Dp,
    private val notchCenterY: Dp,
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density,
    ): Outline {
        val cornerPx: Float = with(density) { cornerRadius.toPx() }
        val notchRadiusPx: Float = with(density) { notchRadius.toPx() }
        val notchCenterYPx: Float = with(density) { notchCenterY.toPx() }

        val body: Path =
            Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(offset = Offset.Zero, size = size),
                        cornerRadius = CornerRadius(cornerPx),
                    ),
                )
            }
        val notches: Path =
            Path().apply {
                addOval(Rect(center = Offset(0f, notchCenterYPx), radius = notchRadiusPx))
                addOval(Rect(center = Offset(size.width, notchCenterYPx), radius = notchRadiusPx))
            }

        return Outline.Generic(
            Path().apply { op(body, notches, PathOperation.Difference) },
        )
    }
}

/** 여행지마다 고정된 4자리 발권 번호 */
private fun RandomDestinationModel.serialNumber(): String =
    name
        .hashCode()
        .mod(SERIAL_RANGE)
        .toString()
        .padStart(SERIAL_DIGITS, '0')

private val TICKET_IMAGE_HEIGHT: Dp = 150.dp
private val TICKET_CORNER_RADIUS: Dp = 16.dp
private val NOTCH_RADIUS: Dp = 10.dp
private val TICKET_ELEVATION: Dp = 8.dp
private val PERFORATION_STROKE: Dp = 1.dp

private const val PRINT_DURATION_MILLIS: Int = 900
private const val SCRIM_TOP_ALPHA: Float = 0.35f
private const val SCRIM_BOTTOM_ALPHA: Float = 0.6f
private const val SUBTLE_TEXT_ALPHA: Float = 0.75f
private const val DASH_LENGTH: Float = 12f
private const val DASH_GAP: Float = 10f
private const val SERIAL_RANGE: Int = 10000
private const val SERIAL_DIGITS: Int = 4
