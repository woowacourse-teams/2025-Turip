package com.on.turip.feature.mypage.impl.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun MyPageAppBar(modifier: Modifier = Modifier) {
    TuripAppBar(
        modifier = modifier,
        start = {
            Text(
                text = "마이페이지",
                style = TuripTheme.typography.display,
            )
        },
    )
}
