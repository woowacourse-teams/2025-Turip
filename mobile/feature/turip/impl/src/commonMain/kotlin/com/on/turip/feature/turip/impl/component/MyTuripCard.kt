package com.on.turip.feature.turip.impl.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.model.Turip

@Composable
fun MyTuripCard(
    turip: Turip,
    isDeleteMode: Boolean,
    onTuripClick: (turipId: Long) -> Unit,
    onLongPress: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Card(
            shape = TuripTheme.shape.largeContainer,
            colors = CardDefaults.cardColors(containerColor = TuripTheme.colors.container),
            border = BorderStroke(
                width = 1.dp,
                color = if (isDeleteMode && !turip.isDefault) TuripTheme.colors.error else TuripTheme.colors.border,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = { onTuripClick(turip.id) },
                    onLongClick = { if (!turip.isDefault) onLongPress() },
                ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.large),
                modifier = Modifier.padding(TuripTheme.spacing.large),
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(TuripTheme.shape.chip)
                        .background(TuripTheme.colors.chipBackground),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = if (turip.isShared) Icons.Default.Group else Icons.Default.Folder,
                        contentDescription = null,
                        tint = TuripTheme.colors.primary,
                        modifier = Modifier.size(40.dp),
                    )
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
                ) {
                    TuripTypeChip(isShared = turip.isShared)

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
                        if (turip.isShared) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Group,
                                    contentDescription = null,
                                    tint = TuripTheme.colors.gray03,
                                    modifier = Modifier.size(16.dp),
                                )
                                Text(
                                    text = turip.memberCount.toString(),
                                    style = TuripTheme.typography.info1,
                                )
                            }
                        }

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraSmall),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = TuripTheme.colors.gray03,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = turip.placeCount.toString(),
                                style = TuripTheme.typography.info1,
                            )
                        }
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

        if (isDeleteMode && !turip.isDefault) {
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 6.dp, y = (-6).dp)
                    .size(24.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "삭제",
                    tint = TuripTheme.colors.error,
                )
            }
        }
    }
}

@Composable
private fun TuripTypeChip(
    isShared: Boolean,
    modifier: Modifier = Modifier,
) {
    val (text, backgroundColor, textColor) = if (isShared) {
        Triple("함께 튜립", TuripTheme.colors.chipBackground, TuripTheme.colors.gray03)
    } else {
        Triple("나홀로 튜립", TuripTheme.colors.primary, TuripTheme.colors.white)
    }

    Box(
        modifier = modifier
            .clip(TuripTheme.shape.container)
            .background(backgroundColor)
            .padding(horizontal = TuripTheme.spacing.small, vertical = TuripTheme.spacing.extraSmall),
    ) {
        Text(
            text = text,
            style = TuripTheme.typography.info2,
            color = textColor,
        )
    }
}
