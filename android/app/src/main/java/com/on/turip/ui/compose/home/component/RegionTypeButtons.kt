package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

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
            text = stringResource(R.string.region_type_buttons_domestic),
            isSelected = isSelectedDomestic,
            onClick = { onDomesticClick(true) },
        )
        RegionTypeButton(
            text = stringResource(R.string.region_type_buttons_abroad),
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
