package com.on.turip.ui.compose.main.home.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun RegionTypeButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backgroundColor: Color =
        if (isSelected) colorResource(R.color.turip_blue_11aebf_70) else colorResource(R.color.turip_light_blue_5ac3d5_11)
    val textColor: Color =
        if (isSelected) colorResource(R.color.turip_light_gray_f2f2f2) else colorResource(R.color.turip_gray_b4b4b4)
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        modifier = modifier,
    ) {
        Text(
            text = text,
            style = TuripTypography.titleSmall,
            color = textColor,
        )
    }
}

@Preview(showBackground = true, name = "선택 안된 상태")
@Composable
private fun NotSelectedPreview() {
    RegionTypeButton(
        text = "해외",
        isSelected = false,
        onClick = {},
    )
}

@Preview(showBackground = true, name = "선택 된 상태")
@Composable
private fun SelectedPreview() {
    RegionTypeButton(
        text = "국내",
        isSelected = true,
        onClick = {},
    )
}
