package com.on.turip.feature.randomtravel.impl.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_abroad
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_domestic
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_pull_hint
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_serial
import com.on.turip.core.designsystem.generated.resources.random_travel_ticket_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import com.on.turip.core.ui.util.noRippleClickable
import com.on.turip.feature.randomtravel.impl.model.RandomDestinationModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

/**
 * 릴 아래에 놓이는 배출구와, 그 위로 티켓이 밀려 올라오는 영역.
 *
 * 티켓은 화면에 떠 있는 카드가 아니라 배출구 안쪽에서 시작한다. 대기 중에는 배출구 위가 비어 있고,
 * [isDispensing] 이 되면 티켓이 [PEEK_PROGRESS] 만큼만 걸쳐 나와 멈춘다. 나머지는 사용자가 직접
 * 위로 당겨 뽑아야 하고, 다 뽑히면 [onPullTicket] 으로 다음 화면을 알린다.
 *
 * 배출구 자체는 정적이라 애니메이션하지 않고 안쪽 라인 색만 바뀐다.
 */
@Composable
internal fun TicketDispenser(
    destination: RandomDestinationModel?,
    isDispensing: Boolean,
    reduceMotion: Boolean,
    onPullTicket: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dispenseProgress: Animatable<Float, *> = remember { Animatable(0f) }
    val currentOnPullTicket: () -> Unit by rememberUpdatedState(onPullTicket)
    val scope = rememberCoroutineScope()

    // 드래그 거리를 진행률로 환산하려면 티켓 높이가 필요하다. 배치된 뒤에 채워진다.
    var ticketHeightPx: Float by remember { mutableFloatStateOf(0f) }

    // 티켓과 안내가 한 덩어리로 움직이도록 흔들림은 여기서 한 번만 만들어 둘 다에 넘긴다.
    val bounceDistancePx: Float = with(LocalDensity.current) { BOUNCE_DISTANCE.toPx() }
    val bounce: State<Float> =
        rememberInfiniteTransition().animateFloat(
            initialValue = 0f,
            targetValue = if (reduceMotion) 0f else -bounceDistancePx,
            animationSpec =
                infiniteRepeatable(
                    animation = tween(durationMillis = BOUNCE_DURATION_MILLIS),
                    repeatMode = RepeatMode.Reverse,
                ),
        )

    LaunchedEffect(isDispensing, reduceMotion) {
        if (!isDispensing) {
            dispenseProgress.snapTo(0f)
            return@LaunchedEffect
        }
        if (reduceMotion) {
            dispenseProgress.snapTo(PEEK_PROGRESS)
            return@LaunchedEffect
        }
        dispenseProgress.animateTo(
            targetValue = PEEK_PROGRESS,
            animationSpec =
                tween(durationMillis = DISPENSE_DURATION_MILLIS, easing = LinearOutSlowInEasing),
        )
    }

    /** 손을 뗐을 때. 충분히 당겼으면 끝까지 뽑고, 아니면 걸쳐 있던 자리로 되돌린다. */
    fun settleTicket() {
        scope.launch {
            if (dispenseProgress.value >= PULL_THRESHOLD) {
                dispenseProgress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = PULL_OUT_DURATION_MILLIS),
                )
                currentOnPullTicket()
            } else {
                dispenseProgress.animateTo(
                    targetValue = PEEK_PROGRESS,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                )
            }
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 티켓 이동 영역. 배출구 아래로 내려간 부분은 여기서 잘려 보이지 않는다.
        Box(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clipToBounds(),
            contentAlignment = Alignment.BottomCenter,
        ) {
            if (destination != null) {
                PullHint(
                    progress = dispenseProgress.asState(),
                    bounceOffset = bounce,
                    ticketHeightPx = ticketHeightPx,
                    modifier = Modifier.align(Alignment.BottomCenter),
                )

                TuripTicketStub(
                    destination = destination,
                    hintProgress = dispenseProgress.asState(),
                    bounceOffset = bounce,
                    modifier =
                        Modifier
                            .width(TICKET_WIDTH)
                            .graphicsLayer {
                                translationY = size.height * (1f - dispenseProgress.value)
                            }.onSizeChanged { ticketHeightPx = it.height.toFloat() }
                            .pointerInput(ticketHeightPx) {
                                if (ticketHeightPx <= 0f) return@pointerInput
                                detectVerticalDragGestures(
                                    onDragEnd = { settleTicket() },
                                    onDragCancel = { settleTicket() },
                                ) { change, dragAmount ->
                                    change.consume()
                                    scope.launch {
                                        dispenseProgress.snapTo(
                                            (dispenseProgress.value - dragAmount / ticketHeightPx)
                                                .coerceIn(PEEK_PROGRESS, 1f),
                                        )
                                    }
                                }
                            }
                            // 드래그가 어려운 경우를 위한 대체 동작. 탭해도 끝까지 뽑힌다.
                            .noRippleClickable(
                                onClickLabel = stringResource(Res.string.random_travel_ticket_pull_hint),
                            ) {
                                scope.launch {
                                    dispenseProgress.animateTo(
                                        targetValue = 1f,
                                        animationSpec = tween(durationMillis = PULL_OUT_DURATION_MILLIS),
                                    )
                                    currentOnPullTicket()
                                }
                            },
                )
            }
        }

        DispenserSlot(isActive = isDispensing)
    }
}

/**
 * 티켓을 당기라는 안내. 당기기 시작하면 사라진다.
 *
 * 이동 영역의 위쪽이 아니라 **티켓의 드러난 윗변** 바로 위에 붙는다. 영역은 남는 공간을 모두 차지해서
 * 기기마다 높이가 다른데, 거기에 맞춰 두면 안내가 티켓에서 멀찍이 떨어져 무엇을 가리키는지 흐려진다.
 *
 * 진행률과 흔들림은 [State] 로 받아 그리기 단계에서만 읽는다. 드래그하는 동안 매 프레임 재구성되지 않는다.
 */
@Composable
private fun PullHint(
    progress: State<Float>,
    bounceOffset: State<Float>,
    ticketHeightPx: Float,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .graphicsLayer {
                    val hintStrength: Float = hintAlpha(progress.value)
                    // 티켓이 올라온 만큼 함께 밀려 올라가고, 흔들림도 티켓과 같은 값으로 따라간다.
                    translationY =
                        -(ticketHeightPx * progress.value) - HINT_GAP.toPx() +
                        bounceOffset.value * hintStrength
                    alpha = hintStrength
                },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = null,
            tint = TuripTheme.colors.primary,
            modifier = Modifier.size(HINT_ICON_SIZE),
        )

        Text(
            text = stringResource(Res.string.random_travel_ticket_pull_hint),
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
        )
    }
}

/** 티켓이 나오는 배출구. 정적 크롬이라 위치가 고정돼 있다. */
@Composable
private fun DispenserSlot(
    isActive: Boolean,
    modifier: Modifier = Modifier,
) {
    val mouthColor: Color by animateColorAsState(
        targetValue = if (isActive) TuripTheme.colors.primary else TuripTheme.colors.gray02,
    )

    Box(
        modifier =
            modifier
                .width(SLOT_WIDTH)
                .height(SLOT_HEIGHT)
                .clip(RoundedCornerShape(SLOT_CORNER_RADIUS))
                .background(TuripTheme.colors.container)
                .border(
                    width = SLOT_BORDER_WIDTH,
                    color = TuripTheme.colors.border,
                    shape = RoundedCornerShape(SLOT_CORNER_RADIUS),
                ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TuripTheme.spacing.large)
                    .height(SLOT_MOUTH_HEIGHT)
                    .clip(RoundedCornerShape(SLOT_MOUTH_HEIGHT / 2))
                    .background(mouthColor),
        )
    }
}

/**
 * 배출구에서 나오는 세로형 티켓.
 *
 * 브리핑에서 펼쳐 보는 [TuripPassTicket] 과 같은 외곽선을 쓰되, 배출 연출 동안 한눈에 읽히도록
 * 여행지 · 사진 · 발권 번호만 남긴 짧은 형태다. 걸쳐 있는 동안에는 [hintProgress] 에 맞춰
 * 살짝 위아래로 움직여 당길 수 있다는 걸 알린다.
 */
@Composable
private fun TuripTicketStub(
    destination: RandomDestinationModel,
    hintProgress: State<Float>,
    bounceOffset: State<Float>,
    modifier: Modifier = Modifier,
) {
    val ticketShape: Shape =
        remember {
            TicketShape(
                cornerRadius = STUB_CORNER_RADIUS,
                notchRadius = STUB_NOTCH_RADIUS,
                notchCenterY = STUB_HEADER_HEIGHT,
            )
        }

    Column(
        modifier =
            modifier
                // 걸쳐 있을 때만 흔들리고, 당기기 시작하면 곧바로 멈춘다.
                .graphicsLayer { translationY = bounceOffset.value * hintAlpha(hintProgress.value) }
                .shadow(elevation = STUB_ELEVATION, shape = ticketShape, clip = false)
                .background(color = TuripTheme.colors.white, shape = ticketShape)
                .border(width = STUB_BORDER_WIDTH, color = TuripTheme.colors.cardBorder, shape = ticketShape)
                .clip(ticketShape),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(STUB_HEADER_HEIGHT),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(Res.string.random_travel_ticket_title),
                style = TuripTheme.typography.info2,
                color = TuripTheme.colors.gray02,
            )

            Text(
                text = destination.name,
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.gray05,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = TuripTheme.spacing.extraLarge,
                            vertical = TuripTheme.spacing.extraSmall,
                        ),
            )
        }

        TicketPerforation()

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(TuripTheme.spacing.medium)
                    .height(STUB_IMAGE_HEIGHT)
                    .clip(TuripTheme.shape.container)
                    .background(TuripTheme.colors.primarySub),
        ) {
            AsyncImage(
                model = destination.imageUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = TuripTheme.spacing.medium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.Place,
                contentDescription = null,
                tint = TuripTheme.colors.primary,
                modifier = Modifier.size(STUB_PIN_SIZE),
            )

            Text(
                text =
                    if (destination.isDomestic) {
                        stringResource(Res.string.random_travel_ticket_domestic)
                    } else {
                        stringResource(Res.string.random_travel_ticket_abroad)
                    },
                style = TuripTheme.typography.info1,
                color = TuripTheme.colors.gray03,
                modifier = Modifier.padding(start = TuripTheme.spacing.extraSmall),
            )
        }

        Text(
            text =
                stringResource(Res.string.random_travel_ticket_serial)
                    .formatResource(destination.serialNumber()),
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.gray02,
            modifier =
                Modifier.padding(
                    top = TuripTheme.spacing.extraSmall,
                    bottom = TuripTheme.spacing.medium,
                ),
        )
    }
}

/**
 * 당기기 안내(문구 · 흔들림)의 세기. 걸쳐 있을 때 1이고, 당길수록 0으로 잦아든다.
 */
private fun hintAlpha(progress: Float): Float =
    ((PULL_THRESHOLD - progress) / (PULL_THRESHOLD - PEEK_PROGRESS)).coerceIn(0f, 1f)

private val TICKET_WIDTH: Dp = 168.dp
private val STUB_HEADER_HEIGHT: Dp = 64.dp
private val STUB_IMAGE_HEIGHT: Dp = 76.dp
private val STUB_CORNER_RADIUS: Dp = 14.dp
private val STUB_NOTCH_RADIUS: Dp = 8.dp
private val STUB_ELEVATION: Dp = 8.dp
private val STUB_BORDER_WIDTH: Dp = 1.dp
private val STUB_PIN_SIZE: Dp = 14.dp
private val BOUNCE_DISTANCE: Dp = 4.dp
private val HINT_ICON_SIZE: Dp = 20.dp

/** 안내와 티켓 윗변 사이 간격 */
private val HINT_GAP: Dp = 8.dp

private val SLOT_WIDTH: Dp = 200.dp
private val SLOT_HEIGHT: Dp = 26.dp
private val SLOT_CORNER_RADIUS: Dp = 6.dp
private val SLOT_BORDER_WIDTH: Dp = 1.dp
private val SLOT_MOUTH_HEIGHT: Dp = 5.dp

/** 배출구에 걸쳐 멈추는 지점. 티켓 높이 기준 비율이라 티켓 상단(여행지 이름)까지 보인다. */
private const val PEEK_PROGRESS: Float = 0.38f

/** 손을 뗐을 때 끝까지 뽑히는 최소 진행률 */
private const val PULL_THRESHOLD: Float = 0.7f

private const val DISPENSE_DURATION_MILLIS: Int = 700
private const val PULL_OUT_DURATION_MILLIS: Int = 220
private const val BOUNCE_DURATION_MILLIS: Int = 800
