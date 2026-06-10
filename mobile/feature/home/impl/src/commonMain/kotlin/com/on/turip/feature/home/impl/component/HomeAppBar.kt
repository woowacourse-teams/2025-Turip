package com.on.turip.feature.home.impl.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.app_name
import com.on.turip.core.designsystem.generated.resources.ic_logo
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
internal fun HomeAppBar(modifier: Modifier = Modifier) {
    TuripAppBar(
        start = {
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = stringResource(Res.string.app_name),
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
