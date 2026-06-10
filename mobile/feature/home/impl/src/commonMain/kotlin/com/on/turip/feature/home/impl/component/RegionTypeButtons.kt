package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.region_type_buttons_abroad
import com.on.turip.core.designsystem.generated.resources.region_type_buttons_domestic
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun RegionTypeButtons(
    onDomesticClick: (isDomestic: Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isSelectedDomestic: Boolean = true,
) {
    Row(
        modifier = modifier.padding(top = TuripTheme.spacing.extraExtraLarge),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.small),
    ) {
        RegionTypeButton(
            text = stringResource(Res.string.region_type_buttons_domestic),
            isSelected = isSelectedDomestic,
            onClick = { onDomesticClick(true) },
        )
        RegionTypeButton(
            text = stringResource(Res.string.region_type_buttons_abroad),
            isSelected = !isSelectedDomestic,
            onClick = { onDomesticClick(false) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegionTypeButtonsPreview() {
    TuripTheme {
        RegionTypeButtons(
            onDomesticClick = {},
        )
    }
}
