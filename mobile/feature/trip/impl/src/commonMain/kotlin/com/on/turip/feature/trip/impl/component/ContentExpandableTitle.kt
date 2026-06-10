package com.on.turip.feature.trip.impl.component

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.*
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun ContentExpandableTitle(
    title: String,
    collapsedMaxLines: Int,
    modifier: Modifier = Modifier,
) {
    var isOverFlow by remember { mutableStateOf(false) }
    var expanded by rememberSaveable(title) { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = modifier.animateContentSize()) {
        Text(
            text = title,
            style = TuripTheme.typography.display,
            maxLines = if (expanded) Int.MAX_VALUE else collapsedMaxLines,
            overflow = TextOverflow.Ellipsis,
            onTextLayout = { result ->
                if (!expanded) isOverFlow = result.hasVisualOverflow
            },
        )

        if (isOverFlow || expanded) {
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .clip(TuripTheme.shape.container)
                        .clickable(
                            onClick = { expanded = !expanded },
                            interactionSource = interactionSource,
                            indication = ripple(color = TuripTheme.colors.gray03),
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription =
                        if (expanded) {
                            stringResource(Res.string.all_text_collapse)
                        } else {
                            stringResource(
                                Res.string.all_text_expand,
                            )
                        },
                    tint = TuripTheme.colors.gray03,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ContentExpandableTitlePreview() {
    TuripTheme {
        ContentExpandableTitle(
            title = "제목이 긴 컨텐츠의 경우 한 줄 이상 입력하면 확장 가능",
            collapsedMaxLines = 1,
            modifier = Modifier.height(120.dp),
        )
    }
}
