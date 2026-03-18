package com.on.turip.ui.compose.turipdetail.component

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.on.turip.ui.compose.designsystem.theme.TuripTheme
import com.on.turip.ui.compose.turipdetail.model.ProfileRowStyle
import com.on.turip.ui.compose.turipdetail.model.ProfileRowStyle.Companion.avatarSize
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import androidx.compose.foundation.interaction.MutableInteractionSource

private const val AVATAR_OVERLAP_RATIO = 0.375f

@Composable
fun ProfileRow(
    labels: ImmutableList<String>,
    profileRowStyle: ProfileRowStyle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val visibleLabels = labels.take(3)
    val count = visibleLabels.size
    val avatarSize = profileRowStyle.avatarSize()
    val shape: Shape = RoundedCornerShape(50.dp)

    val overlap = avatarSize * AVATAR_OVERLAP_RATIO
    val groupWidth = avatarSize + (avatarSize - overlap) * (count - 1)

    Box(
        modifier =
            modifier
                .clip(shape)
                .clickable(onClick = onClick)
                .border(
                    width = 1.5.dp,
                    color = TuripTheme.colors.gray02,
                    shape = shape,
                )
                .padding(
                    horizontal = TuripTheme.spacing.medium,
                    vertical = TuripTheme.spacing.small,
                ),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
        ) {
            Box(
                modifier =
                    Modifier
                        .width(groupWidth)
                        .height(avatarSize),
            ) {
                visibleLabels.forEachIndexed { index, label ->
                    Box(
                        modifier =
                            Modifier
                                .offset(x = (avatarSize - overlap) * index)
                                .zIndex((count - index).toFloat()),
                    ) {
                        AvatarCircle(label = label.first(), profileRowStyle = profileRowStyle)
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TuripTheme.colors.gray03,
                modifier = Modifier.size(TuripTheme.spacing.extraLarge),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProfileRowAllVariantsPreview() {
    val sampleLabels = persistentListOf("유", "현", "민")

    TuripTheme {
        Column(
            modifier =
                Modifier
                    .padding(TuripTheme.spacing.extraExtraLarge)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
        ) {
            Text("기본 (48dp)", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = sampleLabels, profileRowStyle = ProfileRowStyle.LARGE, onClick = {})

            Text("중간 (36dp)", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = sampleLabels, profileRowStyle = ProfileRowStyle.MEDIUM, onClick = {})

            Text("작은 (28dp)", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = sampleLabels, profileRowStyle = ProfileRowStyle.SMALL, onClick = {})
        }
    }
}
