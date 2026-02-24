package com.on.turip.ui.compose.folder.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

private const val LONG_PRESS_DELAY_MS = 800L

@Composable
fun MyTuripCard(
    turip: MyTuripModel,
    onTuripClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isDeleteMode: Boolean = false,
    isDefaultFolder: Boolean = false,
    onLongPress: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }

    Box(modifier = modifier) {
        Card(
            shape = TuripTheme.shape.largeContainer,
            colors = CardDefaults.cardColors(containerColor = TuripTheme.colors.white),
            border =
                BorderStroke(
                    width = 1.dp,
                    color = if (isDeleteMode && !isDefaultFolder) TuripTheme.colors.error else TuripTheme.colors.border,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .indication(
                        interactionSource = interactionSource,
                        indication = ripple(),
                    ).pointerInput(onLongPress, onTuripClick) {
                        awaitEachGesture {
                            val down = awaitFirstDown(requireUnconsumed = false)
                            val press = PressInteraction.Press(down.position)
                            interactionSource.tryEmit(press)

                            val pressStartTime = System.currentTimeMillis()

                            val upOrCancel =
                                withTimeoutOrNull(LONG_PRESS_DELAY_MS) {
                                    waitForUpOrCancellation()
                                }

                            val elapsed: Long = System.currentTimeMillis() - pressStartTime

                            when {
                                upOrCancel != null -> {
                                    interactionSource.tryEmit(PressInteraction.Release(press))
                                    upOrCancel.consume()
                                    onTuripClick(turip.id)
                                }

                                elapsed >= LONG_PRESS_DELAY_MS -> {
                                    interactionSource.tryEmit(PressInteraction.Cancel(press))
                                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    onLongPress?.invoke()
                                    waitForUpOrCancellation()
                                }

                                else -> {
                                    interactionSource.tryEmit(PressInteraction.Cancel(press))
                                }
                            }
                        }
                    },
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
                modifier = Modifier.padding(TuripTheme.spacing.large),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_launcher_background),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(88.dp)
                            .clip(TuripTheme.shape.chip),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                ) {
                    TuripTypeChip(type = turip.type)

                    Text(
                        text = turip.name,
                        style = TuripTheme.typography.title1,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (turip.type == TuripType.TOGETHER) {
                            IconWithCount(
                                imageVector = Icons.Default.Person,
                                count = turip.memberCount,
                            )
                        }
                        IconWithCount(
                            imageVector = Icons.Default.LocationOn,
                            count = turip.placeCount,
                        )
                    }
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = TuripTheme.colors.gray02,
                    modifier = Modifier.size(20.dp),
                )
            }
        }

        if (!isDefaultFolder && isDeleteMode && onDeleteClick != null) {
            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .combinedClickable(onClick = onDeleteClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_remove),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }
        }
    }
}

@Composable
private fun TuripTypeChip(
    type: TuripType,
    modifier: Modifier = Modifier,
) {
    val (label: String, backgroundColor: Color) =
        when (type) {
            TuripType.TOGETHER -> "함께 튜립" to Color(0xFFD4E157)
            TuripType.SOLO -> "나홀로 튜립" to Color(0xFF90CAF9)
        }

    Box(
        modifier =
            modifier
                .clip(TuripTheme.shape.container)
                .background(backgroundColor)
                .padding(
                    horizontal = TuripTheme.spacing.small,
                    vertical = TuripTheme.spacing.extraSmall,
                ),
    ) {
        Text(
            text = label,
            style = TuripTheme.typography.info2,
            color = TuripTheme.colors.white,
        )
    }
}

@Composable
private fun IconWithCount(
    imageVector: ImageVector,
    count: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
        modifier = modifier,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = null,
            tint = TuripTheme.colors.gray03,
            modifier = Modifier.size(16.dp),
        )
        Text(
            text = count.toString(),
            style = TuripTheme.typography.info1,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripCardTogetherPreview() {
    TuripTheme {
        MyTuripCard(
            turip =
                MyTuripModel(
                    id = 0L,
                    name = "수원 여행 계획 튜립",
                    type = TuripType.TOGETHER,
                    memberCount = 3,
                    placeCount = 5,
                    isDefault = true,
                ),
            onTuripClick = {},
            isDeleteMode = false,
            isDefaultFolder = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripCardSoloPreview() {
    TuripTheme {
        MyTuripCard(
            turip =
                MyTuripModel(
                    id = 1L,
                    name = "수원 여행 계획 튜립",
                    type = TuripType.SOLO,
                    placeCount = 3,
                    isDefault = false,
                ),
            onTuripClick = {},
            isDeleteMode = false,
            isDefaultFolder = false,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MyTuripCardDeletedPreview() {
    TuripTheme {
        MyTuripCard(
            turip =
                MyTuripModel(
                    id = 0L,
                    name = "수원 여행 계획 튜립",
                    type = TuripType.TOGETHER,
                    memberCount = 3,
                    placeCount = 5,
                    isDefault = false,
                ),
            onTuripClick = {},
            onLongPress = {},
            onDeleteClick = {},
            isDeleteMode = true,
            isDefaultFolder = false,
        )
    }
}
