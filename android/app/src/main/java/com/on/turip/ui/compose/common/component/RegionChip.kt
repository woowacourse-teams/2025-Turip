package com.on.turip.ui.compose.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.theme.TuripTypography

@Composable
fun RegionChip(
    regionName: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = regionName,
        modifier =
            modifier
                .wrapContentSize()
                .background(
                    color = colorResource(R.color.turip_light_beige_FFF4B2),
                    shape = RoundedCornerShape(24.dp),
                ).padding(horizontal = 8.dp, vertical = 2.dp),
        style = TuripTypography.labelLarge,
        color = colorResource(R.color.pure_black_151515),
    )
}

@Preview(showBackground = true)
@Composable
private fun RegionChipPreview() {
    RegionChip(
        regionName = "속초",
    )
}
