package com.on.turip.ui.compose.common.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R

@Composable
fun TuripAppBar(
    canBack: Boolean,
    modifier: Modifier = Modifier,
    onBackNavigate: () -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(60.dp),
    ) {
        if (canBack) {
            Image(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier =
                    Modifier
                        .clickable(onClick = onBackNavigate)
                        .padding(top = 12.dp, start = 14.dp),
            )
        }
        Image(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = null,
            modifier = Modifier.padding(top = 16.dp, start = 20.dp),
        )
    }
}

@Preview(showBackground = true, name = "뒤로가기 버튼 O")
@Composable
private fun BackableTuripAppBarPreview() {
    TuripAppBar(
        canBack = true,
    )
}

@Preview(showBackground = true, name = "뒤로가기 버튼 X")
@Composable
private fun NotBackableTuripAppBarPreview() {
    TuripAppBar(
        canBack = false,
    )
}
