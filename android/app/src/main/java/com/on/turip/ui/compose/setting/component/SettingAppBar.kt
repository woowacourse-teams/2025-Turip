package com.on.turip.ui.compose.setting.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
internal fun SettingAppBar(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        start = {
            Icon(
                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                contentDescription = stringResource(R.string.all_back_description),
                modifier =
                    Modifier
                        .size(24.dp)
                        .clickable(onClick = onBackClick),
            )
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun SettingAppBarPreview() {
    TuripTheme {
        SettingAppBar(
            onBackClick = {},
        )
    }
}
