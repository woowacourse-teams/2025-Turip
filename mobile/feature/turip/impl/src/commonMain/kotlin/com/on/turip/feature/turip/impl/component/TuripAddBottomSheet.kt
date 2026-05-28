package com.on.turip.feature.turip.impl.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.on.turip.core.designsystem.theme.TuripTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuripAddBottomSheet(
    turipName: String,
    isConfirmEnabled: Boolean,
    onNameChanged: (String) -> Unit,
    onConfirmClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = TuripTheme.colors.white,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraLarge)
                .padding(bottom = TuripTheme.spacing.extraLarge)
                .imePadding(),
        ) {
            Text(
                text = "튜립 추가",
                style = TuripTheme.typography.title1,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            OutlinedTextField(
                value = turipName,
                onValueChange = onNameChanged,
                placeholder = { Text("튜립 이름을 입력해주세요") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = TuripTheme.shape.wideButton,
            )

            Spacer(modifier = Modifier.height(TuripTheme.spacing.large))

            Button(
                onClick = onConfirmClick,
                enabled = isConfirmEnabled,
                shape = TuripTheme.shape.wideButton,
                colors = ButtonDefaults.buttonColors(containerColor = TuripTheme.colors.primary),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("추가")
            }
        }
    }
}
