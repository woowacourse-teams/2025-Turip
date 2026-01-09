package com.on.turip.ui.compose.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
fun RegionTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor: Color =
        if (isSelected) TuripTheme.colors.primary else TuripTheme.colors.primarySub
    val textColor: Color =
        if (isSelected) TuripTheme.colors.gray01 else TuripTheme.colors.gray02
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = TuripTheme.typography.title3,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, name = "선택 안된 상태")
@Composable
private fun NotSelectedPreview() {
    TuripTheme {
        RegionTypeButton(
            text = "해외",
            isSelected = false,
            onClick = {},
        )
    }
}

@Preview(showBackground = true, name = "선택 된 상태")
@Composable
private fun SelectedPreview() {
    TuripTheme {
        RegionTypeButton(
            text = "국내",
            isSelected = true,
            onClick = {},
        )
    }
}
