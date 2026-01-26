package com.on.turip.ui.compose.search.regionresult.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.on.turip.R
import com.on.turip.ui.compose.designsystem.component.TuripAppBar
import com.on.turip.ui.compose.designsystem.theme.TuripTheme

@Composable
internal fun RegionResultAppBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        modifier = modifier,
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(36.dp),
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    tint = TuripTheme.colors.black,
                )
            }
        },
        center = {
            Text(
                text = title,
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.black,
            )
        },
    )
}
