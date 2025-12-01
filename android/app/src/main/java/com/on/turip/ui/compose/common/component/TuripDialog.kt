package com.on.turip.ui.compose.common.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTheme
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun TuripDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    confirmButtonColor: Color,
    dismissButtonColor: Color,
    onConfirmation: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Card(
            modifier =
                modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .wrapContentHeight(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(30.dp),
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
        style = TuripTypography.titleLarge,
        modifier = Modifier.padding(top = 12.dp),
    )
}

@Composable
private fun DialogMessageText(message: String) {
    Text(
        text = message,
        textAlign = TextAlign.Center,
        style = TuripTypography.bodyMedium,
        modifier = Modifier.padding(horizontal = 24.dp),
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
                .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Button(
            onClick = onDismissRequest,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = dismissButtonColor),
        ) {
            Text(
                text = dismissText,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.pure_white_ffffff),
                style = TuripTypography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }

        Button(
            onClick = onConfirmation,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = confirmButtonColor),
        ) {
            Text(
                text = confirmText,
                textAlign = TextAlign.Center,
                color = colorResource(R.color.pure_white_ffffff),
                style = TuripTypography.labelLarge,
                modifier = Modifier.padding(vertical = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TuripDialogPreview() {
    TuripTheme {
        TuripDialog(
            title = "로그아웃",
            message = "정말 로그아웃 하시겠습니까?",
            confirmText = "로그아웃",
            dismissText = "취소",
            confirmButtonColor = colorResource(R.color.turip_blue_11aebf_70),
            dismissButtonColor = colorResource(R.color.turip_gray_b4b4b4),
            onConfirmation = {},
            onDismissRequest = { },
            modifier = Modifier.fillMaxSize(),
        )
    }
}
