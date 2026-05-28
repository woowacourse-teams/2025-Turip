package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun RegionTypeButtons(
    onDomesticClick: (isDomestic: Boolean) -> Unit,
    isSelectedDomestic: Boolean = true,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = TuripTheme.spacing.extraExtraLarge),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        RegionTypeButton(text = "국내", isSelected = isSelectedDomestic, onClick = { onDomesticClick(true) })
        RegionTypeButton(text = "해외", isSelected = !isSelectedDomestic, onClick = { onDomesticClick(false) })
    }
}

@Composable
private fun RegionTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.white
    val textColor = if (isSelected) TuripTheme.colors.white else TuripTheme.colors.gray03
    val borderColor = if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.gray02

    Text(
        text = text,
        style = TuripTheme.typography.title3,
        color = textColor,
        modifier = modifier
            .clip(TuripTheme.shape.chip)
            .border(width = 1.dp, color = borderColor, shape = TuripTheme.shape.chip)
            .background(color = backgroundColor, shape = TuripTheme.shape.chip)
            .clickable(onClick = onClick)
            .padding(horizontal = TuripTheme.spacing.large, vertical = TuripTheme.spacing.small),
    )
}
