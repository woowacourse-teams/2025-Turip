package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
internal fun HomeAppBar(modifier: Modifier = Modifier) {
    TuripAppBar(
        start = {
            Image(
                painter = painterResource(R.drawable.ic_logo),
                contentDescription = stringResource(R.string.app_name),
            )
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun HomeAppBarPreview() {
    TuripTheme {
        HomeAppBar()
    }
}
