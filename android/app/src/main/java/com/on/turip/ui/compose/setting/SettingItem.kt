package com.on.turip.ui.compose.setting

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun SettingItem(
    uiModel: SettingModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(uiModel.iconResource),
            contentDescription = null,
            modifier = Modifier.padding(end = 14.dp),
        )
        Text(
            text = stringResource(uiModel.titleResource),
            style = TuripTypography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingItemPreview() {
    val uiModel =
        SettingModel(
            iconResource = R.drawable.ic_heart_pressed,
            titleResource = R.string.setting_privacy_policy,
        )
    SettingItem(
        uiModel = uiModel,
        onClick = {},
        modifier = Modifier.fillMaxSize(),
    )
}
