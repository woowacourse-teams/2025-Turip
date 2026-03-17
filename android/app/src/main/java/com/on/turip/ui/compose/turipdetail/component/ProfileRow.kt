package com.on.turip.ui.compose.turipdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

private val AVATAR_SIZE = 48.dp
private val AVATAR_OVERLAP = 18.dp

@Composable
fun ProfileRow(
    labels: List<String>,
    modifier: Modifier = Modifier,
) {
    val visibleLabels = labels.take(3)
    val count = visibleLabels.size

    val groupWidth = AVATAR_SIZE + (AVATAR_SIZE - AVATAR_OVERLAP) * (count - 1)

    Box(
        modifier =
            modifier
                .border(
                    width = 1.5.dp,
                    color = TuripTheme.colors.gray02,
                    shape = RoundedCornerShape(50.dp),
                ).padding(
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
                        .height(AVATAR_SIZE),
            ) {
                visibleLabels.forEachIndexed { index, label ->
                    Box(
                        modifier =
                            Modifier
                                .offset(x = (AVATAR_SIZE - AVATAR_OVERLAP) * index)
                                .zIndex((count - index).toFloat()),
                    ) {
                        AvatarCircle(label = label)
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "더보기",
                tint = TuripTheme.colors.gray03,
                modifier = Modifier.size(TuripTheme.spacing.extraLarge),
            )
        }
    }
}

@Composable
fun AvatarCircle(
    label: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = TuripTheme.colors.gray05,
    textColor: Color = TuripTheme.colors.white,
) {
    Box(
        modifier =
            modifier
                .size(AVATAR_SIZE)
                .clip(CircleShape)
                .background(backgroundColor)
                .border(width = 2.dp, color = TuripTheme.colors.white, shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun ProfileRowAllVariantsPreview() {
    TuripTheme {
        Column(
            modifier =
                Modifier
                    .padding(TuripTheme.spacing.extraExtraLarge)
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraLarge),
        ) {
            Text("1명", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = listOf("유"))

            Text("2명", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = listOf("유", "현"))

            Text("3명", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = listOf("유", "현", "민"))

            Text("4명 이상 → 앞 3명만 표시", fontSize = 12.sp, color = Color.Gray)
            ProfileRow(labels = listOf("유", "현", "민", "지", "호"))
        }
    }
}
