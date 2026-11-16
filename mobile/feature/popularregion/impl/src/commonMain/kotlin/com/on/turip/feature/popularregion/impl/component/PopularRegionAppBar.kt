package com.on.turip.feature.popularregion.impl.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.on.turip.core.designsystem.component.TuripAppBar
import com.on.turip.core.designsystem.generated.resources.Res
import com.on.turip.core.designsystem.generated.resources.all_back_description
import com.on.turip.core.designsystem.generated.resources.popular_region_title
import com.on.turip.core.designsystem.theme.TuripTheme
import com.on.turip.core.ui.util.formatResource
import org.jetbrains.compose.resources.stringResource

/**
 * @param baseMonthText 방문자 수 기준월 문구. 지도가 어느 시점을 보여주는지 제목에서 바로 읽히게 한다.
 */
@Composable
internal fun PopularRegionAppBar(
    baseMonthText: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TuripAppBar(
        start = {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(ICON_BUTTON_SIZE),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = stringResource(Res.string.all_back_description),
                )
            }
        },
        center = {
            Text(
                text = stringResource(Res.string.popular_region_title).formatResource(baseMonthText),
                style = TuripTheme.typography.title1,
                color = TuripTheme.colors.black,
            )
        },
        modifier = modifier,
    )
}

private val ICON_BUTTON_SIZE = 36.dp

@Preview(showBackground = true)
@Composable
private fun PopularRegionAppBarPreview() {
    TuripTheme {
        PopularRegionAppBar(
            baseMonthText = "2025년 6월",
            onBackClick = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
