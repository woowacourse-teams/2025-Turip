package com.on.turip.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.on.turip.core.designsystem.theme.TuripTheme

@Composable
fun TuripDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonColor: Color = TuripTheme.colors.primary,
    dismissButtonColor: Color = TuripTheme.colors.gray02,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = TuripTheme.spacing.extraLarge)
                    .wrapContentHeight(),
            shape = TuripTheme.shape.largeContainer,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = TuripTheme.spacing.extraLarge),
                verticalArrangement = Arrangement.spacedBy(TuripTheme.spacing.extraHuge),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DialogTitleText(title = title)
                DialogMessageText(message = message)
                DialogButtons(
                    confirmText = confirmText,
                    dismissText = dismissText,
                    confirmButtonColor = confirmButtonColor,
                    dismissButtonColor = dismissButtonColor,
                    onConfirmation = onConfirmation,
                    onDismissRequest = onDismissRequest,
                )
            }
        }
    }
}

@Composable
private fun DialogTitleText(title: String) {
    Text(
        text = title,
        textAlign = TextAlign.Center,
        style = TuripTheme.typography.title1,
        modifier = Modifier.padding(top = TuripTheme.spacing.medium),
    )
}

@Composable
private fun DialogMessageText(message: String) {
    Text(
        text = message,
        textAlign = TextAlign.Center,
        style = TuripTheme.typography.body2,
        modifier = Modifier.padding(horizontal = TuripTheme.spacing.extraExtraLarge),
    )
}

@Composable
private fun DialogButtons(
    confirmText: String,
    dismissText: String,
    confirmButtonColor: Color,
    dismissButtonColor: Color,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = TuripTheme.spacing.extraLarge),
        horizontalArrangement = Arrangement.spacedBy(TuripTheme.spacing.medium),
    ) {
        Button(
            onClick = onDismissRequest,
            modifier = Modifier.weight(1f),
            shape = TuripTheme.shape.container,
            colors = ButtonDefaults.buttonColors(containerColor = dismissButtonColor),
        ) {
            Text(
                text = dismissText,
                textAlign = TextAlign.Center,
                style = TuripTheme.typography.info1,
                modifier = Modifier.padding(vertical = TuripTheme.spacing.small),
            )
        }

        Button(
            onClick = onConfirmation,
            modifier = Modifier.weight(1f),
            shape = TuripTheme.shape.container,
            colors = ButtonDefaults.buttonColors(containerColor = confirmButtonColor),
        ) {
            Text(
                text = confirmText,
                textAlign = TextAlign.Center,
                style = TuripTheme.typography.info1,
                modifier = Modifier.padding(vertical = TuripTheme.spacing.small),
            )
        }
    }
}
