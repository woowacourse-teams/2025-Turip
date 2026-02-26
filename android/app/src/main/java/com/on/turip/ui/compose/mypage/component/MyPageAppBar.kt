package com.on.turip.ui.compose.mypage.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun MyPageAppBar() {
    TuripAppBar(
        start = {
            Text(
                text = stringResource(R.string.my_page_title),
                style = TuripTheme.typography.display,
            )
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun MyPageAppBarPreView() {
    TuripTheme {
        MyPageAppBar()
    }
}
