package com.on.turip.feature.login.impl.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun GuestModeSection(
    onClickGuestLogin: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "둘러보고 싶다면?",
            style = TuripTheme.typography.body2,
            color = TuripTheme.colors.gray03,
        )
        TextButton(onClick = onClickGuestLogin) {
            Text(
                text = "게스트로 시작하기",
                style = TuripTheme.typography.body2,
                color = TuripTheme.colors.primary,
            )
        }
    }
}
