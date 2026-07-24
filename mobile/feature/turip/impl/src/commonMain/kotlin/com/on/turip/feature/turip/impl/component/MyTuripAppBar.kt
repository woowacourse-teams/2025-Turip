package com.on.turip.feature.turip.impl.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.my_turip_screen_title
import com.on.turip.core.designsystem.theme.TuripTheme
import org.jetbrains.compose.resources.stringResource

@Composable
fun MyTuripAppBar() {
    TuripAppBar(
        start = {
            Text(
                text = stringResource(Res.string.my_turip_screen_title),
                style = TuripTheme.typography.display,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MyTuripAppBarPreview() {
    TuripTheme {
        MyTuripAppBar()
    }
}
