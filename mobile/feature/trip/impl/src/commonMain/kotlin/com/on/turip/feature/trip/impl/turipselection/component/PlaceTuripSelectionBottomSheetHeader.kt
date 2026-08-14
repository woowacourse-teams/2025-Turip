package com.on.turip.feature.trip.impl.turipselection.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun PlaceTuripSelectionBottomSheetHeader(
    title: String,
    modifier: Modifier = Modifier,
    navigation: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = TuripTheme.spacing.small),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            navigation?.invoke()
        }

        Text(
            text = title,
            style = TuripTheme.typography.title2,
            color = TuripTheme.colors.gray04,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 60.dp),
        )

        Row(
            modifier = Modifier.align(Alignment.CenterEnd),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions?.invoke(this)
        }
    }
}
